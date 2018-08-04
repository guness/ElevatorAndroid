package com.guness.elevator.ui.pages.panel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.ComponentName
import android.os.IBinder
import com.guness.core.SGViewModel
import com.guness.elevator.R
import com.guness.elevator.SoundManager
import com.guness.elevator.db.ElevatorEntity
import com.guness.elevator.db.OrderEntity
import com.guness.elevator.db.PanelPrefsEntity
import com.guness.elevator.db.PanelPrefsEntity.KeyDef
import com.guness.elevator.model.ElevatorState
import com.guness.utils.SingleLiveEvent
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
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
    private val floorSelected: LiveData<OrderEntity?> = getApp().getDatabase().dao().getOrderLive()
    val buttonPressed: LiveData<Int?> = Transformations.map(floorSelected) {
        if (it?.device == mDevice.value) {
            it?.floor
        } else {
            null
        }
    }
    val preferences: MutableLiveData<List<PanelPrefsEntity>> = MutableLiveData()
    var showFloorPickerCommand: SingleLiveEvent<Triple<String, ElevatorEntity, Int?>> = SingleLiveEvent()

    private var mPreselected: Int? = null

    private var mDevice: BehaviorSubject<String> = BehaviorSubject.create()
    private var mSoundManager = SoundManager(getAppContext())

    override fun onStart() {
        super.onStart()
        entity.value ?: mDevice.take(1)
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
        preferences.value ?: subscribeUntilDetach(mDevice
                .take(1)
                .flatMap { uuid ->
                    getApp().getDatabase()
                            .dao()
                            .getPanelPrefs(uuid)
                            .toObservable()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    preferences.value = it
                }, {
                    preferences.value = null
                    Timber.e(it, "Error fetching preferences")
                }))
        mSoundManager.createSoundPool()
    }

    override fun onServiceConnected(className: ComponentName, binder: IBinder) {
        super.onServiceConnected(className, binder)

        mDevice.take(1)
                .subscribe { uuid ->
                    subscribeUntilDetach(service!!.stateObservable
                            .filter { it.device == uuid }
                            .subscribeOn(Schedulers.computation())
                            .observeOn(Schedulers.io())
                            .subscribe {
                                elevatorState.postValue(it)

                                floorSelected.value?.let { orderEntity ->
                                    if (orderEntity.device == uuid && orderEntity.floor == it.floor && it.action == ElevatorState.STOP) {
                                        getApp().getDatabase().dao().clearOrder()
                                        mSoundManager.playDing()
                                    }
                                    if (mPreselected != null) {
                                        Single.just(mPreselected!!)
                                                .delay(200, TimeUnit.MILLISECONDS)
                                                .subscribeOn(Schedulers.computation())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(Consumer {
                                                    onFloorSelected(it)
                                                })
                                        mPreselected = null
                                    }
                                }
                            })

                    subscribeUntilDetach(service!!.orderObservable
                            .filter { it.order?.device == uuid }
                            .map {
                                elevatorError.postValue(if (it.success) {
                                    val orderEntity = OrderEntity()
                                    orderEntity.floor = it.order?.floor
                                    orderEntity.device = uuid
                                    getApp().getDatabase().dao().insert(orderEntity)
                                    ""
                                } else {
                                    getApp().getDatabase().dao().clearOrder()
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

    override fun onCleared() {
        super.onCleared()
        mDevice.take(1)
                .subscribe { uuid ->
                    service!!.sendStopListenDevice(uuid)
                }
        mSoundManager.release()
    }

    fun onFloorSelected(floor: Int) {
        val uuid = mDevice.value
        if (uuid != null) {
            mSoundManager.playClick()
            service?.sendRelayOrder(uuid, floor)
        }
    }

    fun onFavoriteClicked(@KeyDef key: String) {
        val favorite = preferences.value?.find { key == it.key }
        if (favorite == null) {
            showFloorPickerCommand.value = Triple(key, entity.value!!, null)
        } else {
            onFloorSelected(favorite.floor)
        }
    }

    fun onFavoriteLongClicked(@KeyDef key: String) {
        val favorite = preferences.value?.find { key == it.key }
        showFloorPickerCommand.value = Triple(key, entity.value!!, favorite?.floor)
    }

    fun setFloorPreSelected(floor: Int) {
        mPreselected = floor
    }

    fun setDevice(uuid: String) {
        mDevice.onNext(uuid)
    }

    fun onFavoriteFloorPicked(@KeyDef key: String, entity: ElevatorEntity, floor: Int) {
        Single.just(PanelPrefsEntity())
                .observeOn(Schedulers.io())
                .subscribe(Consumer {
                    it.key = key
                    it.floor = floor
                    it.device = entity.device
                    it.groupId = entity.groupId
                    getApp().getDatabase().dao().insert(it)
                })
    }

    fun onFloorRemoved(@KeyDef key: String) {
        mDevice.take(1)
                .observeOn(Schedulers.io())
                .subscribe({
                    getApp().getDatabase().dao().deletePanelPref(key, it)
                })
    }
}