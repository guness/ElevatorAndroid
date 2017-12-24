package com.guness.elevator.ui.panel

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.guness.BackgroundService
import com.guness.core.SGActivity
import com.guness.elevator.R

class PanelActivity : SGActivity() {

    private lateinit var mViewModel: PanelViewModel

    override fun getViewModel() = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel)

        mViewModel = ViewModelProviders.of(this).get(PanelViewModel::class.java)

        startService(Intent(this, BackgroundService::class.java))
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, PanelActivity::class.java)
        }
    }
}
