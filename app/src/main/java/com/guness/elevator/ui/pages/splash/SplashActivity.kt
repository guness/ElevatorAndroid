package com.guness.elevator.ui.pages.splash

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.guness.core.SGActivity
import com.guness.elevator.R
import timber.log.Timber


/**
 * Created by guness on 16.12.2017.
 */
class SplashActivity : SGActivity() {

    private lateinit var mViewModel: SplashViewModel

    override fun getViewModel() = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    if (pendingDynamicLinkData != null) {
                        mViewModel.onDeepLinkDetected(pendingDynamicLinkData.link)
                    }
                }
                .addOnFailureListener(this) { e -> Timber.w(e, "getDynamicLink:onFailure") }
    }
}
