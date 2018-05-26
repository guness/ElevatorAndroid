package com.guness.elevator.model

import android.support.annotation.StringDef


/**
 * Created by guness on 24.12.2017.
 */
class Fetch {

    @InfoTypeDef
    var type: String = ""
    //TODO: ideally we should not fetch by ID, instead use key
    var id: Long = 0
    var uuid: String? = null

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(TYPE_GROUP)
    annotation class InfoTypeDef

    companion object {
        const val TYPE_GROUP = "Group"
        const val TYPE_UUID = "Uuid"
        const val TYPE_KEY = "Key" //TODO: use this to fetch
    }
}