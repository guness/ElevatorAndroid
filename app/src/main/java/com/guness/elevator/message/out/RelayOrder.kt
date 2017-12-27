package com.guness.elevator.message.out

import com.guness.elevator.message.AbstractMessage
import com.guness.elevator.model.Order

/**
 * Created by guness on 24.12.2017.
 */
class RelayOrder() : AbstractMessage() {
    var order: Order? = null


    constructor(order: Order?) : this() {
        this.order = order
    }
}