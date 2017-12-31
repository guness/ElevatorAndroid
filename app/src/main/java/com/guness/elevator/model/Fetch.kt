package com.guness.elevator.model

import android.support.annotation.StringDef


/**
 * Created by guness on 24.12.2017.
 */
class Fetch {

    @InfoTypeDef
    var type: String = ""
    var id: Long = 0

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(TYPE_GROUP)
    annotation class InfoTypeDef

    companion object {
        const val TYPE_GROUP = "Group"
    }
}