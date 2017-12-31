package com.guness.elevator.ui.panel

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.guness.core.SGActivity
import com.guness.elevator.R
import com.guness.elevator.ui.panel.PanelAdapter.FloorClickedListener
import kotlinx.android.synthetic.main.activity_panel.*

class PanelActivity : SGActivity() {

    private lateinit var mViewModel: PanelViewModel
    private lateinit var mAdapter: PanelAdapter

    override fun getViewModel() = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mAdapter = PanelAdapter()
        listView.adapter = mAdapter
        listView.setHasFixedSize(true)

        mViewModel = ViewModelProviders.of(this).get(PanelViewModel::class.java)
        mViewModel.device = intent.getStringExtra(EXTRA_UUID)
        mViewModel.entity.observe(this, Observer {
            title = it?.description
            mAdapter.setElevator(it)
            listView.scrollToPosition(0)
        })
        mViewModel.elevatorState.observe(this, Observer {
            sevenSegment.text = when {
                it == null -> null
                it.online -> it.floor.toString()
                else -> getString(R.string.panel_off)
            }
        })
        mViewModel.elevatorError.observe(this, Observer {
            errorView.text = it
        })

        mAdapter.listener = object : FloorClickedListener {
            override fun onClick(floor: Int) {
                mViewModel.onFloorSelected(floor)
            }
        }
    }

    companion object {
        private const val EXTRA_UUID = "uuid"
        fun newIntent(context: Context, uuid: String): Intent {
            return Intent(context, PanelActivity::class.java)
                    .putExtra(EXTRA_UUID, uuid)
        }
    }
}
