package com.guness.elevator.message.`in`

import com.guness.elevator.message.AbstractMessage
import com.guness.elevator.model.Order

/**
 * Created by guness on 24.12.2017.
 */
class RelayOrderResponse : AbstractMessage() {
    var order: Order? = null
    var success = false
}