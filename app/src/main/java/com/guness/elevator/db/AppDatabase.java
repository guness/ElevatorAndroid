package com.guness.elevator.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {ElevatorEntity.class, GroupEntity.class, SettingsEntity.class, FavoriteEntity.class, PanelPrefsEntity.class}, version = 11)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDao dao();
}
