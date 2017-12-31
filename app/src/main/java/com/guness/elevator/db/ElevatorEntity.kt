package com.guness.elevator.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.guness.elevator.model.Elevator

/**
 * Created by guness on 18.12.2017.
 */
@Entity(tableName = AppDao.TABLE_ELEVATOR,
        indices = [Index("groupId")],
        foreignKeys = [(ForeignKey(entity = GroupEntity::class,
                parentColumns = ["id"],
                childColumns = ["groupId"],
                onDelete = ForeignKey.CASCADE))])
class ElevatorEntity {

    @PrimaryKey
    var id: Long = 0
    var device: String = ""
    var groupId: Long = 0
    var minFloor: Int = 0
    var floorCount: Int = 0
    var address: String? = null
    var description: String? = null
    var latitude: Double? = null
    var longitude: Double? = null

    companion object {

        fun fromElevator(elevator: Elevator, groupId: Long): ElevatorEntity {
            val entity = ElevatorEntity()
            entity.id = elevator.id
            entity.device = elevator.device
            entity.groupId = groupId
            entity.minFloor = elevator.minFloor
            entity.floorCount = elevator.floorCount
            entity.address = elevator.address
            entity.description = elevator.description
            entity.latitude = elevator.latitude
            entity.longitude = elevator.longitude
            return entity
        }
    }
}