package com.guness.elevator.ui.panel

import android.annotation.TargetApi
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.IBinder
import com.guness.core.SGViewModel
import com.guness.elevator.R
import com.guness.elevator.db.ElevatorEntity
import com.guness.elevator.model.ElevatorState
import com.guness.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 * Created by guness on 23.12.2017.
 */
class PanelViewModel(application: Application) : SGViewModel(application) {
    var entity: MutableLiveData<ElevatorEntity> = MutableLiveData()
    var elevatorState: MutableLiveData<ElevatorState> = MutableLiveData()
    var elevatorError: MutableLiveData<String> = SingleLiveEvent()

    var device: String? = null
    private var mSound: SoundPool? = null
    private var mClickSound = 0

    override fun onStart() {
        super.onStart()
        if (device != null) {
            getApp().getDatabase()
                    .dao()
                    .getElevator(device!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        entity.value = it
                    }, {
                        entity.value = null
                        Timber.e(it, "Error fetching elevator")
                    })
        }
        createSoundPool()
    }

    override fun onServiceConnected(className: ComponentName, binder: IBinder) {
        super.onServiceConnected(className, binder)

        subscribeUntilDetach(service!!.stateObservable
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    elevatorState.value = it
                })

        subscribeUntilDetach(service!!.orderObservable
                .map {
                    elevatorError.postValue(if (it.success) {
                        ""
                    } else {
                        getAppContext().getString(R.string.service_failed)
                    })
                    ""
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .delay(2000, TimeUnit.MILLISECONDS)
                .subscribe { elevatorError.postValue(it) })
    }

    private fun createSoundPool() {
        mSound = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool()
        } else {
            createOldSoundPool()
        }
        mClickSound = mSound!!.load(getAppContext(), R.raw.button_sound, 1) // in 2nd param u have to pass your desire ringtone
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createNewSoundPool(): SoundPool {
        val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        return SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build()
    }

    @Suppress("DEPRECATION")
    private fun createOldSoundPool(): SoundPool {
        return SoundPool(5, AudioManager.STREAM_MUSIC, 0)
    }

    fun onFloorSelected(floor: Int) {
        if (device != null) {
            mSound?.play(mClickSound, 1f, 1f, 1, 0, 1f)
            service?.sendRelayOrder(device!!, floor)
        }
    }
}