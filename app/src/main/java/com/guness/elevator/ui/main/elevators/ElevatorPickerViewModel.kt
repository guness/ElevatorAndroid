package com.guness.elevator.ui.main.elevators

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.guness.core.SGApplication
import com.guness.elevator.db.ElevatorEntity
import com.guness.elevator.ui.ElevatorAdapter

/**
 * Created by guness on 13.01.2018.
 */
class ElevatorPickerViewModel(application: Application) : AndroidViewModel(application), ElevatorAdapter.OnElevatorClickedListener {
    val elevators = getApplication<SGApplication>().getDatabase().dao().getElevators()
    val groups = getApplication<SGApplication>().getDatabase().dao().getGroupsLive()
    var key: String? = null

    override fun onElevatorClicked(entity: ElevatorEntity) {

    }
}