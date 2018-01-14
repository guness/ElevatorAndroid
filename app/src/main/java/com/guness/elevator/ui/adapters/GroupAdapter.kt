package com.guness.elevator.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.guness.elevator.R
import com.guness.elevator.db.GroupEntity
import kotlinx.android.synthetic.main.item_group.view.*

/**
 * Created by guness on 12.01.2018.
 */
class GroupAdapter : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    private val mList: ArrayList<GroupEntity> = ArrayList()
    private var mSelectedGroup: GroupEntity? = null
    var listener: OnGroupClickedListener? = null

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun getItemCount() = mList.size

    fun setList(list: List<GroupEntity>?) {
        mList.clear()
        if (list != null) {
            mList.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun onGroupSelected(entity: GroupEntity) {
        mSelectedGroup = entity
        notifyDataSetChanged()
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var mEntity: GroupEntity? = null

        init {
            itemView.textView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener?.onGroupClicked(mEntity!!)
            onGroupSelected(mEntity!!)
        }

        fun bind(entity: GroupEntity) {
            mEntity = entity
            itemView.textView.text = entity.description
            if (mSelectedGroup == entity) {
                itemView.textView.setBackgroundResource(R.drawable.metallic_border)
            } else {
                itemView.textView.setBackgroundResource(0)
            }
        }
    }

    interface OnGroupClickedListener {
        fun onGroupClicked(entity: GroupEntity)
    }
}