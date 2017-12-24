package com.guness.elevator.ui.main

import android.app.Application
import com.guness.core.SGViewModel
import com.guness.elevator.ui.panel.PanelActivity

class MainViewModel(application: Application) : SGViewModel(application) {
    override fun onStart() {
        super.onStart()
        launchCommand.value = Pair(false, PanelActivity.newIntent(getAppContext()))
    }
}