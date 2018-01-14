package com.guness.elevator.ui.pages.info

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
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
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, InfoActivity::class.java)
        }
    }
}
