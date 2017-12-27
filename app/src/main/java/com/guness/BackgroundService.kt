package com.guness

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.guness.core.SGApplication
import com.guness.elevator.Constants.WS_HOST
import com.guness.elevator.db.ElevatorEntity
import com.guness.elevator.db.GroupEntity
import com.guness.elevator.message.AbstractMessage
import com.guness.elevator.message.`in`.GroupInfo
import com.guness.elevator.message.`in`.RelayOrderResponse
import com.guness.elevator.message.`in`.UpdateState
import com.guness.elevator.message.out.*
import com.guness.elevator.model.ElevatorState
import com.guness.elevator.model.Fetch
import com.guness.elevator.model.Order
import com.guness.utils.RuntimeTypeAdapterFactory
import io.reactivex.subjects.BehaviorSubject
import okhttp3.*
import timber.log.Timber


class BackgroundService : Service() {

    private val mBinder: Binder
    private val mClient: OkHttpClient
    private val mGson: Gson
    private var mWS: WebSocket? = null
    private val mStateObservable: BehaviorSubject<ElevatorState> = BehaviorSubject.create()
    private val mOrderResponseObservable: BehaviorSubject<RelayOrderResponse> = BehaviorSubject.create()

    init {

        mBinder = LocalBinder()

        mClient = OkHttpClient.Builder()
                .authenticator { _, response ->
                    val credential = Credentials.basic("Sinan", "Gunes")
                    response.request()
                            .newBuilder()
                            .header("Authorization", credential)
                            .build()
                }
                .build()
        val rta = RuntimeTypeAdapterFactory.of(AbstractMessage::class.java, "_type")
                .registerSubtype(Echo::class.java)
                .registerSubtype(FetchInfo::class.java)
                .registerSubtype(GroupInfo::class.java)
                .registerSubtype(ListenDevice::class.java)
                .registerSubtype(RelayOrder::class.java)
                .registerSubtype(RelayOrderResponse::class.java)
                .registerSubtype(StopListening::class.java)
                .registerSubtype(UpdateState::class.java)
        mGson = GsonBuilder()
                .registerTypeAdapterFactory(rta)
                .create()
    }

    private val mWebSocketListener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            Timber.i("onMessage: " + text)
            val message: AbstractMessage? = try {
                mGson.fromJson(text, AbstractMessage::class.java)
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
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Timber.e("onFailure response: $response by: $t")
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.e("Starting Service")
        val request = Request.Builder()
                .url(WS_HOST)
                .build()

        mWS = mClient.newWebSocket(request, mWebSocketListener)
        val fetch = Fetch()
        fetch.type = Fetch.TYPE_GROUP
        fetch.id = 1
        sendPacket(FetchInfo(fetch))

        val deviceUUID = "UUID-0000-000-001"

        sendPacket(ListenDevice(deviceUUID))

        val order = Order()
        order.floor = 6
        order.device = deviceUUID
        //sendPacket(RelayOrder(order))
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    private fun sendPacket(data: AbstractMessage) {
        mWS!!.send(mGson.toJson(data, AbstractMessage::class.java))
    }

    inner class LocalBinder : Binder() {
        internal val service: BackgroundService
            get() = this@BackgroundService
    }
}
