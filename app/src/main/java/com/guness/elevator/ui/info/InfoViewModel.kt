package com.guness.elevator.ui.info

import android.app.Application
import com.guness.core.SGViewModel
import com.guness.elevator.ui.main.MainActivity

/**
 * Created by guness on 23.12.2017.
 */
class InfoViewModel(application: Application) : SGViewModel(application) {
    override fun onStart() {
        super.onStart()
        launchCommand.value = Pair(true, MainActivity.newIntent(getAppContext()))
    }
}