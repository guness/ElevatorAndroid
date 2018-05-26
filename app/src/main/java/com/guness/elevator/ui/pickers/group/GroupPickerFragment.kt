package com.guness.elevator.ui.pickers.group

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.guness.elevator.R
import com.guness.elevator.db.GroupEntity
import com.guness.elevator.ui.adapters.GroupAdapter
import kotlinx.android.synthetic.main.fragment_picker_group.*

/**
 * A simple [Fragment] subclass.
 */
class GroupPickerFragment : Fragment() {
    private lateinit var mAdapter: GroupAdapter
    private lateinit var mViewModel: GroupPickerViewModel
    private var mListener: GroupPickerListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(GroupPickerViewModel::class.java)
        mAdapter = GroupAdapter()

        mViewModel.groups.observe(this, Observer {
            mAdapter.setList(it)
        })
        mAdapter.listener = object : GroupAdapter.OnGroupClickedListener {
            override fun onGroupClicked(entity: GroupEntity) {
                mViewModel.selected = entity
                removeButton.isEnabled = true
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picker_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.adapter = mAdapter
        removeButton.setOnClickListener {
            if (mViewModel.selected != null) {
                mListener?.onGroupRemoved(mViewModel.selected!!)
            }
        }
        cancelButton.setOnClickListener {
            mListener?.onCancelled()
        }
    }

    fun setListener(listener: GroupPickerListener) {
        mListener = listener
    }

    interface GroupPickerListener {
        fun onGroupRemoved(entity: GroupEntity)
        fun onCancelled()
    }

    companion object {
        fun newInstance() = GroupPickerFragment()
    }

}// Required empty public constructor
