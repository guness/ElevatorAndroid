package com.guness.elevator.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.guness.elevator.R
import com.guness.elevator.db.ElevatorEntity

/**
 * Created by guness on 27.12.2017.
 */
class FloorAdapter : RecyclerView.Adapter<FloorAdapter.PanelButtonHolder>() {

    private var entity: ElevatorEntity? = null
    var listener: FloorClickedListener? = null
    private var mSelectedFloor: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PanelButtonHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)
        return PanelButtonHolder(view)
    }

    override fun onBindViewHolder(holder: PanelButtonHolder, position: Int) {
        holder.bind(positionToFloor(position))
    }

    override fun getItemCount() = entity?.floorCount ?: 0


    fun setElevator(entity: ElevatorEntity?) {
        this.entity = entity
        notifyDataSetChanged()
    }

    private fun positionToFloor(position: Int): Int {
        return position + (entity?.minFloor ?: 0)
    }

    private fun floorToPosition(floor: Int): Int {
        return floor - (entity?.minFloor ?: 0)
    }

    fun onButtonSelected(floor: Int?) {
        var oldPos = -1
        if (mSelectedFloor != null) {
            oldPos = floorToPosition(mSelectedFloor!!)
        }
        mSelectedFloor = floor
        if (oldPos >= 0) {
            notifyItemChanged(oldPos)
        }
        if (floor != null) {
            notifyItemChanged(floorToPosition(floor))
        }
    }

    inner class PanelButtonHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(button: View) {
            listener?.onClick(button.tag as Int)
        }

        fun bind(floor: Int) {
            (itemView as Button).text = floor.toString()
            itemView.setBackgroundResource(if (floor == mSelectedFloor) R.drawable.button_highlight else R.drawable.button)
            itemView.setTag(floor)
        }
    }

    interface FloorClickedListener {
        fun onClick(floor: Int)
    }
}