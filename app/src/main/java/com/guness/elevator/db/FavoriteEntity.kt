package com.guness.elevator.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.StringDef

/**
 * Created by guness on 13.01.2018.
 */
@Entity(tableName = AppDao.TABLE_FAVORITE,
        indices = [(Index("device"))])
//TODO: add foreign key with onDelete=NoAction
class FavoriteEntity {

    @KeyDef
    @PrimaryKey
    var key: String = ""

    @TypeDef
    var type: String = ""
    var device: String = ""
    var floor: Int? = null
    var description: String? = null


    @Retention(AnnotationRetention.SOURCE)
    @StringDef(L1, L2, L3, L4, R1, R2, R3, R4)
    annotation class KeyDef

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(TYPE_ELEVATOR, TYPE_FLOOR)
    annotation class TypeDef

    companion object {
        const val L1 = "L1"
        const val L2 = "L2"
        const val L3 = "L3"
        const val L4 = "L4"

        const val R1 = "R1"
        const val R2 = "R2"
        const val R3 = "R3"
        const val R4 = "R4"

        const val TYPE_ELEVATOR = "elevator"
        const val TYPE_FLOOR = "floor"
    }
}