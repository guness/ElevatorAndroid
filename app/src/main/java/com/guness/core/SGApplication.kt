package com.guness.core

import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.facebook.stetho.Stetho
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.guness.elevator.BuildConfig
import com.guness.utils.CrashlyticsTree
import io.fabric.sdk.android.Fabric
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * Created by guness on 9.12.2017.
 */
class SGApplication : MultiDexApplication() {

    //private lateinit var mDatabase: AppDatabase

    val deviceSubject: BehaviorSubject<String> = BehaviorSubject.create()
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

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
/*
        mDatabase = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app-database")
                .build()
*/
        //InitService.startService(this)

        //BackgroundService.Helper.startService(this)

        Stetho.initializeWithDefaults(this)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            setDeviceRef(user.uid)
        }
    }

    /*
        fun getDatabase(): AppDatabase {
            return mDatabase
        }
    */
    fun setDeviceRef(uid: String) {
        deviceSubject.onNext(uid)
    }

    fun setActive(active: Boolean) {
        activeSubject.onNext(active)
    }
}
