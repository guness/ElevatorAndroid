package com.guness.elevator.ui.panel

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.guness.core.SGActivity
import com.guness.elevator.R

class PanelActivity : SGActivity() {

    private lateinit var mViewModel: PanelViewModel

    override fun getViewModel() = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel)

        mViewModel = ViewModelProviders.of(this).get(PanelViewModel::class.java)
    }
}
