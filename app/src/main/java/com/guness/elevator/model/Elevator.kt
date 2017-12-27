package com.guness.elevator.model

import com.google.gson.annotations.SerializedName

/**
 * Created by guness on 24.12.2017.
 */
class Elevator {
    var id: Long = 0
    var device: String = ""

    @SerializedName("min_floor")
    var minFloor: Int = 0

    @SerializedName("floor_count")
    var floorCount: Int = 0

    var address: String? = null
    var description: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
}