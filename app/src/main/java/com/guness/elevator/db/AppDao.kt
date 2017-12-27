package com.guness.elevator.db

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE


/**
 * Created by guness on 18.12.2017.
 */
@Dao
abstract class AppDao {

    @Query("SELECT value FROM $TABLE_SETTINGS WHERE key = :key")
    abstract fun getSettingsFor(key: String): String?

    @Insert(onConflict = REPLACE)
    abstract fun setSettings(settings: SettingsEntity)

    @Insert(onConflict = REPLACE)
    abstract fun insert(elevators: List<ElevatorEntity>)

    @Insert(onConflict = REPLACE)
    abstract fun insert(group: GroupEntity)

    @Delete
    abstract fun delete(group: GroupEntity)

    @Transaction
    open fun insertGroup(group: GroupEntity, elevators: List<ElevatorEntity>) {
        delete(group)
        insert(group)
        if (elevators.isNotEmpty()) {
            insert(elevators)
        }
    }

    companion object {
        const val TABLE_ELEVATOR = "elevator"
        const val TABLE_GROUP = "group"
        const val TABLE_SETTINGS = "settings"
    }
}