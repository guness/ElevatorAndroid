package com.guness.elevator.db

import android.arch.persistence.room.Relation

/**
 * Created by guness on 25.12.2017.
 */
class GroupWithDevices {
    var id: Long = 0
    var description: String? = null
    @Relation(parentColumn = "id", entityColumn = "groupId")
    var elevators: List<ElevatorEntity>? = null
}