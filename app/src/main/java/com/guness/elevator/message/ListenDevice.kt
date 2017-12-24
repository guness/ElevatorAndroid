package com.guness.elevator.message

/**
 * Created by guness on 24.12.2017.
 */
class ListenDevice() : AbstractMessage() {
    var device: String = ""

    constructor(device: String) : this() {
        this.device = device
    }
}