package com.guness.elevator.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

/**
 * Created by guness on 19.12.2017.
 */
@Entity(tableName = AppDao.TABLE_SETTINGS, indices = [Index(value = ["key"], unique = true)])
class SettingsEntity(val key: String, val value: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    companion object {
        const val UUID = "uuid"
    }
}