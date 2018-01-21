package com.guness.elevator.service

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import timber.log.Timber

/**
 * Created by guness on 21.01.2018.
 */

class SGFirebaseInstanceIDService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        Timber.e("TOKEN: %s", FirebaseInstanceId.getInstance().token)
    }
}
