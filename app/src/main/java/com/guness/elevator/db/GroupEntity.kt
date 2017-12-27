package com.guness.elevator.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.guness.elevator.model.Group

/**
 * Created by guness on 25.12.2017.
 */
@Entity(tableName = AppDao.TABLE_GROUP)
class GroupEntity {

    @PrimaryKey
    var id: Long = 0
    var description: String? = null

    companion object {
        fun fromGroup(group: Group): GroupEntity {
            val entity = GroupEntity()
            entity.id = group.id
            entity.description = group.description
            return entity
        }
    }
}
