package com.guness.elevator.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.StringDef

/**
 * Created by guness on 13.01.2018.
 */
@Entity(tableName = AppDao.TABLE_PANEL,
        indices = [(Index("elevatorId")), (Index("elevatorId", "key", unique = true))])
//TODO: add foreign key with onDelete=NoAction
class PanelPrefsEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @KeyDef
    var key: String = ""

    var elevatorId: Long = 0
    var floor: Int = 0

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(B1, B2, B3, B4)
    annotation class KeyDef

    companion object {
        const val B1 = "B1"
        const val B2 = "B2"
        const val B3 = "B3"
        const val B4 = "B4"
    }
}