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
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 * Created by guness on 23.12.2017.
 */
class PanelViewModel(application: Application) : SGViewModel(application) {
    val entity: MutableLiveData<ElevatorEntity> = MutableLiveData()
    val elevatorState: MutableLiveData<ElevatorState> = MutableLiveData()
    val elevatorError: MutableLiveData<String> = SingleLiveEvent()
    val buttonPressed: MutableLiveData<Int?> = SingleLiveEvent()
    var floorSelected: Int? = null

    private var device: BehaviorSubject<String> = BehaviorSubject.create()

    private var mSound: SoundPool? = null
    private var mClickSound = 0
    private var mDingSound = 0

    override fun onStart() {
        super.onStart()
        device.take(1)
                .subscribe { uuid ->
                    getApp().getDatabase()
                            .dao()
                            .getElevator(uuid)
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

        device.take(1)
                .subscribe { uuid ->
                    subscribeUntilDetach(service!!.stateObservable
                            .filter { it.device == uuid }
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                elevatorState.value = it
                                if (floorSelected == it.floor) {
                                    floorSelected = null
                                    buttonPressed.value = null
                                    mSound?.play(mDingSound, 1f, 1f, 1, 0, 1f)
                                }
                            })

                    subscribeUntilDetach(service!!.orderObservable
                            .filter { it.order?.device == uuid }
                            .map {
                                elevatorError.postValue(if (it.success) {
                                    buttonPressed.postValue(it.order?.floor)
                                    ""
                                } else {
                                    buttonPressed.postValue(null)
                                    getAppContext().getString(R.string.service_failed)
                                })
                                ""
                            }
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .delay(2000, TimeUnit.MILLISECONDS)
                            .subscribe { elevatorError.postValue(it) })

                    service!!.sendListenDevice(uuid)
                }
    }

    override fun onStop() {
        super.onStop()
        device.take(1)
                .subscribe { uuid ->
                    service!!.sendStopListenDevice(uuid)
                }
    }

    private fun createSoundPool() {
        mSound = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool()
        } else {
            createOldSoundPool()
        }
        mClickSound = mSound!!.load(getAppContext(), R.raw.elevator_button, 1) // in 2nd param u have to pass your desire ringtone
        mDingSound = mSound!!.load(getAppContext(), R.raw.elevator_ding, 1)
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
        val uuid = device.value
        if (uuid != null) {
            floorSelected = floor
            mSound?.play(mClickSound, 1f, 1f, 1, 0, 1f)
            service?.sendRelayOrder(uuid, floor)
        }
    }

    fun setDevice(uuid: String) {
        device.onNext(uuid)
    }
}