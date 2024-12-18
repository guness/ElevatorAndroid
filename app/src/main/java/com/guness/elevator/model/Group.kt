package com.guness.elevator.model

/**
 * Created by guness on 24.12.2017.
 */
class Group {
    var id: Long = 0
    var uuid: String = ""
    var description: String? = null
    var address: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var elevators: Array<Elevator>? = null
}