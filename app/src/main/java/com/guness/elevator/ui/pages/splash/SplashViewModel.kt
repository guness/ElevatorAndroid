package com.guness.elevator.ui.pages.splash

import android.app.Application
import android.net.Uri
import com.guness.core.SGApplication
import com.guness.core.SGViewModel
import com.guness.elevator.ui.pages.info.InfoActivity
import com.guness.elevator.ui.pages.main.MainActivity
import com.guness.elevator.ui.pages.scan.ScanActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Created by guness on 16.12.2017.
 */
class SplashViewModel(application: Application) : SGViewModel(application) {

    private var mDeepLink: Uri? = null
    override fun onStart() {
        super.onStart()
        val app: SGApplication = getApplication()
        app.getDatabase()
                .dao()
                .getGroupCount()
                .delay(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    launchCommand.value = if (mDeepLink == null) {
                        if (it > 0) {
                            Pair(true, MainActivity.newIntent(getAppContext()))
                        } else {
                            Pair(true, ScanActivity.newIntent(getAppContext()))
                        }
                    } else {
                        Pair(true, InfoActivity.newIntent(getAppContext(), mDeepLink!!))
                    }
                })
    }

    fun onDeepLinkDetected(link: Uri?) {
        mDeepLink = link
        Timber.d("DeepLink detected: " + link)
    }
}