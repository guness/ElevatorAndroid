package com.guness.elevator.ui.main

import android.app.Application
import android.arch.lifecycle.LiveData
import com.guness.core.SGViewModel
import com.guness.elevator.db.GroupWithDevices
import com.guness.elevator.ui.panel.PanelActivity

class MainViewModel(application: Application) : SGViewModel(application) {

    var groups: LiveData<List<GroupWithDevices>> = getApp().getDatabase().dao().getAllDevices()

    override fun onStart() {
        super.onStart()
        // launchCommand.value = Pair(false, PanelActivity.newIntent(getAppContext()))
    }

    fun onElevatorSelected(groupId: Int, itemId: Int) {
        val device = groups.value?.find { it.group?.id?.toInt() == groupId }
                ?.elevators?.find { it.id.toInt() == itemId }
                ?.device
        if (device != null) {
            launchCommand.value = Pair(false, PanelActivity.newIntent(getAppContext(), device))
        }
    }

    fun onAddElevatorClicked(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onDeleteElevatorClicked(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}