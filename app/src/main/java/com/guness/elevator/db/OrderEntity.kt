package com.guness.elevator.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by guness on 25.12.2017.
 */
//TODO: add foreign key with onDelete=NoAction for device
@Entity(tableName = AppDao.TABLE_ORDER)
class OrderEntity {

    @PrimaryKey
    var id: Long = 1
    var device: String = ""
    var floor: Int? = null
}
