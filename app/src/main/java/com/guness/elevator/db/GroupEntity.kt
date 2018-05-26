package com.guness.elevator.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.guness.elevator.model.Group

/**
 * Created by guness on 25.12.2017.
 */
@Entity(tableName = AppDao.TABLE_GROUP,
        indices = [Index("uuid", unique = true)])
class GroupEntity {

    @PrimaryKey
    var id: Long = 0
    var uuid: String = ""
    var address: String? = null
    var description: String? = null
    var latitude: Double? = null
    var longitude: Double? = null

    companion object {
        fun fromGroup(group: Group): GroupEntity {
            val entity = GroupEntity()
            entity.id = group.id
            entity.uuid = group.uuid
            entity.description = group.description
            entity.address = group.address
            entity.latitude = group.latitude
            entity.longitude = group.longitude
            return entity
        }
    }
}
