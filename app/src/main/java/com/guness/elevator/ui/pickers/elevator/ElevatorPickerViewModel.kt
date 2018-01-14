package com.guness.elevator.ui.pickers.elevator

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.guness.core.SGApplication

/**
 * Created by guness on 13.01.2018.
 */
class ElevatorPickerViewModel(application: Application) : AndroidViewModel(application) {
    val elevators = getApplication<SGApplication>().getDatabase().dao().getElevators()
    val groups = getApplication<SGApplication>().getDatabase().dao().getGroupsLive()
}