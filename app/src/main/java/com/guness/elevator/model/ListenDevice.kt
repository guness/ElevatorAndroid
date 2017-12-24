package com.guness.elevator.model

/**
 * Created by guness on 24.12.2017.
 */
class ListenDevice() : AbstractModel() {

    constructor(device: String) : this() {
        this.device = device
    }

    var device: String = ""
}