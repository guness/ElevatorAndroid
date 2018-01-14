package com.guness.elevator.ui.pages.scan

import android.app.Application
import android.net.Uri
import com.guness.core.SGViewModel
import com.guness.elevator.ui.pages.info.InfoActivity

/**
 * Created by guness on 23.12.2017.
 */
class ScanViewModel(application: Application) : SGViewModel(application) {

    fun onDeepLinkDetected(link: Uri) {
        launchCommand.value = Pair(true, InfoActivity.newIntent(getAppContext(), link))
    }
}