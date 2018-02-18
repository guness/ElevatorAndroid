package com.guness.elevator.message.inc

import com.guness.elevator.message.AbstractMessage
import com.guness.elevator.model.Group

/**
 * Created by guness on 24.12.2017.
 */
class GroupInfo : AbstractMessage() {
    var group: Group? = null
}