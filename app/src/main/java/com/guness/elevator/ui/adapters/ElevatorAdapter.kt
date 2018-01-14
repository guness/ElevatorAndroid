package com.guness.elevator.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.guness.elevator.R
import com.guness.elevator.db.ElevatorEntity
import com.guness.elevator.db.GroupEntity
import kotlinx.android.synthetic.main.item_elevator.view.*

/**
 * Created by guness on 12.01.2018.
 */
class ElevatorAdapter : RecyclerView.Adapter<ElevatorAdapter.ElevatorViewHolder>() {

    private val mList: ArrayList<ElevatorEntity> = ArrayList()
    private val mGroupList: ArrayList<GroupEntity> = ArrayList()
    var listener: OnElevatorClickedListener? = null
    private var mSelectedElevator: String? = null

    override fun onBindViewHolder(holder: ElevatorViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElevatorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_elevator, parent, false)
        return ElevatorViewHolder(view)
    }

    override fun getItemCount() = mList.size

    fun setGroups(list: List<GroupEntity>?) {
        mGroupList.clear()
        if (list != null) {
            mGroupList.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun setList(list: List<ElevatorEntity>?) {
        mList.clear()
        if (list != null) {
            mList.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun isFirstAtGroup(position: Int): Boolean {
        return position == 0 ||
                mList[position].groupId != mList[position - 1].groupId

    }

    fun getGroupTitle(position: Int): String? {
        val groupId = mList[position].groupId
        return mGroupList.find { it.id == groupId }?.description
    }


    fun onElevatorSelected(device: String?) {
        mSelectedElevator = device
        notifyDataSetChanged()
    }

    inner class ElevatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var mEntity: ElevatorEntity? = null

        init {
            itemView.buttonView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener?.onElevatorClicked(mEntity!!)
        }

        fun bind(entity: ElevatorEntity) {
            itemView.buttonView.isEnabled = mSelectedElevator != entity.device
            mEntity = entity
            itemView.textView.text = entity.description
        }
    }

    interface OnElevatorClickedListener {
        fun onElevatorClicked(entity: ElevatorEntity)
    }
}