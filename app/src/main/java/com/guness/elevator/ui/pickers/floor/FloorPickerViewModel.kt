package com.guness.elevator.ui.pickers.floor

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.guness.core.SGApplication
import com.guness.elevator.db.ElevatorEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by guness on 13.01.2018.
 */
class FloorPickerViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var mDevice: String
    var elevator: MutableLiveData<ElevatorEntity> = MutableLiveData()

    fun setDevice(device: String) {
        mDevice = device
        getApplication<SGApplication>().getDatabase()
                .dao()
                .getElevator(device)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    elevator.value = it
                }, {
                    elevator.value = null
                    Timber.e(it, "Error fetching elevator")
                })
    }
}