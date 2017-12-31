package com.guness.elevator.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import io.reactivex.Single


/**
 * Created by guness on 18.12.2017.
 */
@Dao
abstract class AppDao {

    @Query("SELECT value FROM $TABLE_SETTINGS WHERE key = :key")
    abstract fun getSettingsFor(key: String): String?

    @Insert(onConflict = REPLACE)
    abstract fun putSettings(settings: SettingsEntity)

    @Insert(onConflict = REPLACE)
    abstract fun insert(elevators: List<ElevatorEntity>)

    @Insert(onConflict = REPLACE)
    abstract fun insert(group: GroupEntity)

    @Delete
    abstract fun delete(group: GroupEntity)

    @Query("SELECT * FROM $TABLE_ELEVATOR WHERE device = :device")
    abstract fun getElevator(device: String): Single<ElevatorEntity>

    @Query("SELECT * FROM $TABLE_GROUP")
    abstract fun getAllDevices(): LiveData<List<GroupWithDevices>>

    @Query("SELECT * FROM $TABLE_GROUP")
    abstract fun getGroups(): Single<List<GroupEntity>>

    @Transaction
    open fun insertGroup(group: GroupEntity, elevators: List<ElevatorEntity>) {
        delete(group)
        insert(group)
        if (elevators.isNotEmpty()) {
            insert(elevators)
        }
    }

    companion object {
        private const val PREFIX = "a_"
        const val TABLE_ELEVATOR = PREFIX + "elevator"
        const val TABLE_GROUP = PREFIX + "group"
        const val TABLE_SETTINGS = PREFIX + "settings"
    }
}