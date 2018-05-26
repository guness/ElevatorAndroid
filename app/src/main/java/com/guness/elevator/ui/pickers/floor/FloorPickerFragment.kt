package com.guness.elevator.ui.pickers.floor

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.guness.elevator.R
import com.guness.elevator.ui.adapters.FloorAdapter
import kotlinx.android.synthetic.main.fragment_picker_floor.*

/**
 * A simple [Fragment] subclass.
 */
class FloorPickerFragment : Fragment() {
    private lateinit var mAdapter: FloorAdapter
    private lateinit var mViewModel: FloorPickerViewModel
    private var mListener: FloorPickerListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(FloorPickerViewModel::class.java)
        mViewModel.setDevice(arguments!!.getString(EXTRA_DEVICE)!!)
        mAdapter = FloorAdapter()
        val selected = arguments!!.getInt(EXTRA_PICKED)
        if (selected > 0) {
            mAdapter.onButtonSelected(selected)
        }
        mViewModel.elevator.observe(this, Observer {
            mAdapter.setElevator(it)
        })
        mAdapter.listener = object : FloorAdapter.FloorClickedListener {
            override fun onClick(floor: Int) {
                mListener?.onFloorPicked(floor)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_picker_floor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.adapter = mAdapter
        removeButton.setOnClickListener {
            mListener?.onFloorRemoved()
        }
        cancelButton.setOnClickListener {
            mListener?.onCancelled()
        }
    }

    fun setListener(listener: FloorPickerListener) {
        mListener = listener
    }

    interface FloorPickerListener {
        fun onFloorPicked(floor: Int)
        fun onFloorRemoved()
        fun onCancelled()
    }

    companion object {
        private val EXTRA_PICKED = "EXTRA_PICKED"
        private val EXTRA_DEVICE = "EXTRA_DEVICE"
        fun newInstance(device: String, floor: Int?): FloorPickerFragment {
            val fragment = FloorPickerFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_DEVICE, device)
            if (floor != null) {
                bundle.putInt(EXTRA_PICKED, floor)
            }
            fragment.arguments = Bundle(bundle)
            return fragment
        }
    }

}// Required empty public constructor
