package com.guness.elevator.model

import android.support.annotation.StringDef
import com.guness.elevator.model.Fetch.Companion.TYPE_GROUP

/**
 * Created by guness on 27.12.2017.
 */
class ElevatorState {
    var device: String = ""
    var online = false
    var floor = 0
    var busy = false

    @DirectionDef
    var direction: String? = null


    @Retention(AnnotationRetention.SOURCE)
    @StringDef(TYPE_GROUP)
    annotation class DirectionDef

    companion object {
        const val UP = "UP"
        const val DOWN = "DOWN"
    }
}