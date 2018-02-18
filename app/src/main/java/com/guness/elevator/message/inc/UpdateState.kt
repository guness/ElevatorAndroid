package com.guness.elevator.message.inc

import com.guness.elevator.message.AbstractMessage
import com.guness.elevator.model.ElevatorState

/**
 * Created by guness on 24.12.2017.
 */
class UpdateState : AbstractMessage() {
    var state: ElevatorState? = null
}