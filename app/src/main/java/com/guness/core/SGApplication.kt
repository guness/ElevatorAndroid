package com.guness.core

import android.arch.persistence.room.Room
import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.facebook.stetho.Stetho
import com.guness.elevator.BuildConfig
import com.guness.elevator.db.AppDatabase
import com.guness.utils.CrashlyticsTree
import io.fabric.sdk.android.Fabric
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * Created by guness on 9.12.2017.
 */
class SGApplication : MultiDexApplication() {

    private lateinit var mDatabase: AppDatabase

    val activeSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    override fun onCreate() {
        super.onCreate()

        val core = CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build()
        Fabric.with(this, Crashlytics.Builder().core(core).build())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }

        mDatabase = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app-database")
                .build()

        //InitService.startService(this)

        //BackgroundService.Helper.startService(this)

        Stetho.initializeWithDefaults(this)
    }


    fun getDatabase(): AppDatabase {
        return mDatabase
    }

    fun setActive(active: Boolean) {
        activeSubject.onNext(active)
    }
}
