package com.guness.elevator.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.google.firebase.iid.FirebaseInstanceId
import com.guness.core.SGApplication
import com.guness.elevator.Constants.WS_HOST
import com.guness.elevator.db.ElevatorEntity
import com.guness.elevator.db.GroupEntity
import com.guness.elevator.db.SettingsEntity
import com.guness.elevator.message.AbstractMessage
import com.guness.elevator.message.AbstractMessage.Companion.GSON
import com.guness.elevator.message.inc.GroupInfo
import com.guness.elevator.message.inc.RelayOrderResponse
import com.guness.elevator.message.inc.UpdateState
import com.guness.elevator.message.out.FetchInfo
import com.guness.elevator.message.out.ListenDevice
import com.guness.elevator.message.out.RelayOrder
import com.guness.elevator.message.out.StopListening
import com.guness.elevator.model.ElevatorState
import com.guness.elevator.model.Fetch
import com.guness.elevator.model.Order
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import okhttp3.*
import okhttp3.internal.ws.RealWebSocket
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


class BackgroundService : Service() {

    private val mBinder = LocalBinder()
    private var mClient: OkHttpClient? = null
    private var mWS: RealWebSocket? = null
    private val mGroupInfoObservable: PublishSubject<GroupInfo> = PublishSubject.create()
    private val mStateObservable: PublishSubject<ElevatorState> = PublishSubject.create()
    private val mOrderResponseObservable: PublishSubject<RelayOrderResponse> = PublishSubject.create()

    val groupInfoObservable: Observable<GroupInfo>
        get() = mGroupInfoObservable

    val stateObservable: Observable<ElevatorState>
        get() = mStateObservable

    val orderObservable: Observable<RelayOrderResponse>
        get() = mOrderResponseObservable

    private val mWebSocketListener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            Timber.i("onMessage: " + text)
            val message: AbstractMessage? = try {
                GSON.fromJson(text, AbstractMessage::class.java)
            } catch (e: Exception) {
                Timber.e(e, "Cannot parse message: $text")
                null
            }
            val app = application as SGApplication
            when (message) {
                is UpdateState -> {
                    val state = message.state
                    if (state != null) {
                        mStateObservable.onNext(state)
                    }
                }
                is GroupInfo -> {
                    val group = message.group
                    if (group != null) {
                        val groupEntity = GroupEntity.fromGroup(group)
                        val elevatorEntities = group.elevators?.map {
                            ElevatorEntity.fromElevator(it, group.id)
                        } ?: emptyList()

                        app.getDatabase().dao().insertGroup(groupEntity, elevatorEntities)
                        mGroupInfoObservable.onNext(message)
                    }
                }
                is RelayOrderResponse -> {
                    mOrderResponseObservable.onNext(message)
                }
                else -> {
                    if (message != null) {
                        Timber.e("Unhandled message: " + message)
                    }
                }
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Timber.e("onClosed reason: " + reason)
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            if (response.code() != 101) {
                Timber.e("onOpen response: " + response)
            }
/*
            val fetch1 = Fetch()
            fetch1.type = Fetch.TYPE_GROUP
            fetch1.id = 1
            sendPacket(FetchInfo(fetch1))

            val fetch2 = Fetch()
            fetch2.type = Fetch.TYPE_GROUP
            fetch2.id = 2
            sendPacket(FetchInfo(fetch2))

            val fetch3 = Fetch()
            fetch3.type = Fetch.TYPE_GROUP
            fetch3.id = 3
            sendPacket(FetchInfo(fetch3))
*/
            (application as SGApplication).getDatabase()
                    .dao()
                    .getGroups()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe({ groups ->
                        groups.forEach {
                            val fetch = Fetch()
                            fetch.type = Fetch.TYPE_GROUP
                            fetch.id = it.id
                            sendPacket(FetchInfo(fetch))
                        }
                    }, {
                        Timber.e(it, "Error fetching groups")
                    })
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Timber.e("onFailure response: $response by: $t")
            synchronized(this) {
                if (mWS == webSocket) {
                    mWS = newWebSocket()
                    Single.just(mWS)
                            .delay(10000, TimeUnit.MILLISECONDS)
                            .subscribe(Consumer {
                                mWS!!.connect(mClient)
                            })
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("Starting Service")

        getUUID()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(Consumer {
                    val token = FirebaseInstanceId.getInstance().token ?: "NULL"
                    mClient = OkHttpClient.Builder()
                            .pingInterval(60000, TimeUnit.MILLISECONDS)
                            .authenticator { _, response ->
                                val credential = Credentials.basic(it, token)
                                response.request()
                                        .newBuilder()
                                        .header("Authorization", credential)
                                        .build()
                            }
                            .build()
                    mWS = newWebSocket()
                    mWS!!.connect(mClient)
                })
    }

    private fun newWebSocket(): RealWebSocket {
        val request = Request.Builder()
                .url(WS_HOST)
                .build()
        return RealWebSocket(request, mWebSocketListener, Random())
    }

    private fun getUUID(): Single<String> {
        return Single.fromCallable {
            val dao = (application as SGApplication).getDatabase().dao()
            dao.getSettingsFor(SettingsEntity.UUID) ?: {
                val uuid = UUID.randomUUID().toString()
                dao.putSettings(SettingsEntity(SettingsEntity.UUID, uuid))
                uuid
            }.invoke()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    private fun sendPacket(data: AbstractMessage) {
        mWS!!.send(GSON.toJson(data, AbstractMessage::class.java))
    }

    fun sendRelayOrder(device: String, floor: Int) {
        val order = Order()
        order.floor = floor
        order.device = device
        sendPacket(RelayOrder(order))
    }

    fun sendListenDevice(device: String) {
        sendPacket(ListenDevice(device))
    }

    fun sendStopListenDevice(device: String) {
        sendPacket(StopListening(device))
    }

    fun fetchUUID(uuid: UUID) {
        val fetch = Fetch()
        fetch.type = Fetch.TYPE_UUID
        fetch.uuid = uuid.toString()
        sendPacket(FetchInfo(fetch))
    }

    inner class LocalBinder : Binder() {
        internal val service: BackgroundService
            get() = this@BackgroundService
    }
}
