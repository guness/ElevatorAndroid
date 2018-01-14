package com.guness.elevator.ui.pages.info

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.guness.core.SGActivity
import com.guness.elevator.R

class InfoActivity : SGActivity() {

    private lateinit var mViewModel: InfoViewModel

    override fun getViewModel() = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        mViewModel = ViewModelProviders.of(this).get(InfoViewModel::class.java)
        mViewModel.setLink(intent.getParcelableExtra(EXTRA_LINK))
    }

    companion object {
        private val EXTRA_LINK = "EXTRA_LINK"

        fun newIntent(context: Context, link: Uri): Intent {
            return Intent(context, InfoActivity::class.java)
                    .putExtra(EXTRA_LINK, link)
        }
    }
}
