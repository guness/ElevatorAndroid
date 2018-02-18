package com.guness.elevator.message

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.guness.elevator.message.inc.GroupInfo
import com.guness.elevator.message.inc.RelayOrderResponse
import com.guness.elevator.message.inc.UpdateState
import com.guness.elevator.message.out.*
import com.guness.utils.RuntimeTypeAdapterFactory

/**
 * Created by guness on 24.12.2017.
 */
open class AbstractMessage {
    var version = 2


    companion object {
        private val rta = RuntimeTypeAdapterFactory.of(AbstractMessage::class.java, "_type")
                .registerSubtype(GroupInfo::class.java)
                .registerSubtype(RelayOrderResponse::class.java)
                .registerSubtype(UpdateState::class.java)

                .registerSubtype(Echo::class.java)
                .registerSubtype(FetchInfo::class.java)
                .registerSubtype(ListenDevice::class.java)
                .registerSubtype(RelayOrder::class.java)
                .registerSubtype(StopListening::class.java)

        val GSON: Gson = GsonBuilder()
                .registerTypeAdapterFactory(rta)
                .create()
    }
}