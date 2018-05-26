package com.guness.elevator.ui.pages.panel

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.guness.core.SGActivity
import com.guness.elevator.R
import com.guness.elevator.db.PanelPrefsEntity
import com.guness.elevator.ui.adapters.FloorAdapter
import com.guness.elevator.ui.adapters.FloorAdapter.FloorClickedListener
import com.guness.elevator.ui.pickers.floor.FloorPickerFragment
import kotlinx.android.synthetic.main.activity_panel.*

class PanelActivity : SGActivity(), View.OnClickListener, View.OnLongClickListener {

    private lateinit var mViewModel: PanelViewModel
    private lateinit var mAdapter: FloorAdapter

    override fun getViewModel() = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mAdapter = FloorAdapter()
        listView.adapter = mAdapter
        listView.setHasFixedSize(true)

        mViewModel = ViewModelProviders.of(this).get(PanelViewModel::class.java)
        mViewModel.setDevice(intent.getStringExtra(EXTRA_UUID))
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
            if (it?.online != true) {
                mAdapter.onButtonSelected(null)
            }
        })
        mViewModel.elevatorError.observe(this, Observer {
            errorView.text = it
        })
        mViewModel.buttonPressed.observe(this, Observer {
            mAdapter.onButtonSelected(it)
        })
        mViewModel.preferences.observe(this, Observer { list ->
            button1.text = "+"
            button2.text = "+"
            button3.text = "+"
            button4.text = "+"
            list?.forEach {
                when (it.key) {
                    PanelPrefsEntity.B1 -> button1
                    PanelPrefsEntity.B2 -> button2
                    PanelPrefsEntity.B3 -> button3
                    PanelPrefsEntity.B4 -> button4
                    else -> throw IllegalArgumentException("Unsupported button")
                }.text = it.floor.toString()
            }
        })
        mViewModel.showFloorPickerCommand.observe(this, Observer {
            if (it != null) {
                val newFragment = FloorPickerFragment.newInstance(it.second.device, it.third)

                newFragment.setListener(object : FloorPickerFragment.FloorPickerListener {
                    override fun onFloorPicked(floor: Int) {
                        mViewModel.onFavoriteFloorPicked(it.first, it.second, floor)
                        dismissFragment()
                    }

                    override fun onFloorRemoved() {
                        mViewModel.onFloorRemoved(it.first)
                        dismissFragment()
                    }

                    override fun onCancelled() {
                        dismissFragment()
                    }
                })

                showFragment(newFragment)
            }
        })
        mAdapter.listener = object : FloorClickedListener {
            override fun onClick(floor: Int) {
                mViewModel.onFloorSelected(floor)
            }
        }
        val floor = intent.getIntExtra(EXTRA_FLOOR, Int.MIN_VALUE)
        if (floor != Int.MIN_VALUE) {
            mViewModel.setFloorPreSelected(floor)
        }
        button1.setOnClickListener(this)
        button2.setOnClickListener(this)
        button3.setOnClickListener(this)
        button4.setOnClickListener(this)

        button1.setOnLongClickListener(this)
        button2.setOnLongClickListener(this)
        button3.setOnLongClickListener(this)
        button4.setOnLongClickListener(this)
    }


    override fun onClick(v: View?) {
        mViewModel.onFavoriteClicked(when (v) {
            button1 -> PanelPrefsEntity.B1
            button2 -> PanelPrefsEntity.B2
            button3 -> PanelPrefsEntity.B3
            button4 -> PanelPrefsEntity.B4
            else -> throw IllegalArgumentException("Unsupported button")
        })
    }

    override fun onLongClick(v: View?): Boolean {
        mViewModel.onFavoriteLongClicked(when (v) {
            button1 -> PanelPrefsEntity.B1
            button2 -> PanelPrefsEntity.B2
            button3 -> PanelPrefsEntity.B3
            button4 -> PanelPrefsEntity.B4
            else -> throw IllegalArgumentException("Unsupported button")
        })
        return true
    }

    companion object {
        private const val EXTRA_UUID = "EXTRA_UUID"
        private const val EXTRA_FLOOR = "EXTRA_FLOOR"
        fun newIntent(context: Context, uuid: String, floor: Int? = null): Intent {
            val intent = Intent(context, PanelActivity::class.java)
                    .putExtra(EXTRA_UUID, uuid)
            if (floor != null) {
                intent.putExtra(EXTRA_FLOOR, floor)
            }
            return intent
        }
    }
}
