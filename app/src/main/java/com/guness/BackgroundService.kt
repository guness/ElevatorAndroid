package com.guness

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.guness.elevator.Constants.WS_HOST
import com.guness.elevator.model.*
import com.guness.utils.RuntimeTypeAdapterFactory
import okhttp3.*
import timber.log.Timber


class BackgroundService : Service() {

    private val mBinder: Binder
    private val mClient: OkHttpClient
    private val mGson: Gson
    private var mWS: WebSocket? = null

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

        val rta = RuntimeTypeAdapterFactory.of(AbstractModel::class.java, "_type")
                .registerSubtype(Echo::class.java)
                .registerSubtype(ListenDevice::class.java)
                .registerSubtype(RelayOrder::class.java)
                .registerSubtype(RelayOrderResponse::class.java)
                .registerSubtype(StopListening::class.java)
        mGson = GsonBuilder()
                .registerTypeAdapterFactory(rta)
                .create()

    }

    private val mWebSocketListener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket?, text: String?) {
            Timber.e("onMessage: " + text);
        }

        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
            super.onClosed(webSocket, code, reason)
            Timber.e("onClosed reason: " + reason)
        }

        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            super.onOpen(webSocket, response)
            Timber.e("onOpen response: " + response)
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            super.onFailure(webSocket, t, response)
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

        sendPacket(ListenDevice("sinan"))
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    private fun sendPacket(data: AbstractModel) {
        mWS!!.send(mGson.toJson(data, AbstractModel::class.java))
    }

    inner class LocalBinder : Binder() {
        internal val service: BackgroundService
            get() = this@BackgroundService
    }
}
