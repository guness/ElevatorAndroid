package com.guness.elevator.message.out

import com.guness.elevator.message.AbstractMessage

/**
 * Created by guness on 24.12.2017.
 */
class StopListening() : AbstractMessage() {
    var device: String = ""

    constructor(device: String) : this() {
        this.device = device
    }
}