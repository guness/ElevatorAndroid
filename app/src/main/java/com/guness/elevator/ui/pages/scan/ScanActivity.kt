package com.guness.elevator.ui.pages.scan

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.zxing.integration.android.IntentIntegrator
import com.guness.core.SGActivity
import com.guness.elevator.R
import timber.log.Timber


class ScanActivity : SGActivity() {

    private lateinit var mViewModel: ScanViewModel

    override fun getViewModel() = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        mViewModel = ViewModelProviders.of(this).get(ScanViewModel::class.java)

        val integrator = IntentIntegrator(this)
        integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                onBackPressed()
            } else {
                val uri = Uri.parse(result.contents)
                FirebaseDynamicLinks.getInstance()
                        .getDynamicLink(uri)
                        .addOnSuccessListener(this) { pendingDynamicLinkData ->
                            if (pendingDynamicLinkData != null) {
                                mViewModel.onDeepLinkDetected(pendingDynamicLinkData.link)
                            }
                        }
                        .addOnFailureListener(this) { e -> Timber.w(e, "getDynamicLink:onFailure") }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
            onBackPressed()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ScanActivity::class.java)
        }
    }
}
