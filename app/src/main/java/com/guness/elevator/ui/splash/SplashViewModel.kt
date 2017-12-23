package com.guness.elevator.ui.splash

import android.app.Application
import com.guness.core.SGViewModel
import com.guness.elevator.ui.info.InfoActivity

/**
 * Created by guness on 16.12.2017.
 */
class SplashViewModel(application: Application) : SGViewModel(application) {
    override fun onStart() {
        super.onStart()
        /*val app: SGApplication = getApplication()
        app.isLoaded()
                .delay(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (app.hasAuth()) {
                        launchCommand.value = Pair(true, InfoActivity.newIntent(getAppContext()))
                    } else {
                        launchCommand.value = Pair(true, LoginActivity.newIntent(getAppContext()))
                    }
                })
                */
        launchCommand.value = Pair(true, InfoActivity.newIntent(getAppContext()))
    }
}