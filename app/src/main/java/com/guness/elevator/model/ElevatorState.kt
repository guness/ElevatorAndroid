package com.guness.elevator.model

import android.support.annotation.StringDef

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

    @ActionDef
    var action: String? = null


    @Retention(AnnotationRetention.SOURCE)
    @StringDef(UP, DOWN)
    annotation class DirectionDef

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(STOP, PASS)
    annotation class ActionDef


    companion object {
        const val UP = "UP"
        const val DOWN = "DOWN"

        const val STOP = "STOP"
        const val PASS = "PASS"
    }
}