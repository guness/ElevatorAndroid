package com.guness.elevator.db

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation


/**
 * Created by guness on 25.12.2017.
 */
class GroupWithDevices {

    @Embedded
    var group: GroupEntity? = null

    @Relation(parentColumn = "id", entityColumn = "groupId")
    var elevators: List<ElevatorEntity>? = null
}