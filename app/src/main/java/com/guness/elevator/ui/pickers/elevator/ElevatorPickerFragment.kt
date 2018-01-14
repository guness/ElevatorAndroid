package com.guness.elevator.ui.pickers.elevator


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.guness.elevator.R
import com.guness.elevator.db.ElevatorEntity
import com.guness.elevator.ui.adapters.ElevatorAdapter
import kotlinx.android.synthetic.main.fragment_picker_elevator.*


/**
 * A simple [Fragment] subclass.
 */
class ElevatorPickerFragment : DialogFragment() {
    private lateinit var mAdapter: ElevatorAdapter
    private lateinit var mViewModel: ElevatorPickerViewModel
    private var mListener: ElevatorPickerListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(ElevatorPickerViewModel::class.java)
        mAdapter = ElevatorAdapter()
        val picked = arguments?.getString(EXTRA_PICKED)
        if (picked != null) {
            mAdapter.onElevatorSelected(picked)
        }

        mViewModel.groups.observe(this, Observer {
            mAdapter.setGroups(it)
        })
        mViewModel.elevators.observe(this, Observer {
            mAdapter.setList(it)
        })
        mAdapter.listener = object : ElevatorAdapter.OnElevatorClickedListener {
            override fun onElevatorClicked(entity: ElevatorEntity) {
                mListener?.onElevatorPicked(entity)
                dismiss()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_picker_elevator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.adapter = mAdapter
        listView.addItemDecoration(GroupDecorator(mAdapter))
        removeButton.setOnClickListener {
            mListener?.onElevatorRemoved()
            dismiss()
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    fun setListener(listener: ElevatorPickerListener) {
        mListener = listener
    }

    interface ElevatorPickerListener {
        fun onElevatorPicked(entity: ElevatorEntity)
        fun onElevatorRemoved()
    }

    companion object {
        private val EXTRA_PICKED = "EXTRA_PICKED"
        fun newInstance(device: String?): ElevatorPickerFragment {
            val fragment = ElevatorPickerFragment()
            val bundle = Bundle()
            if (device != null) {
                bundle.putString(EXTRA_PICKED, device)
            }
            fragment.arguments = Bundle(bundle)
            return fragment
        }
    }

}// Required empty public constructor
