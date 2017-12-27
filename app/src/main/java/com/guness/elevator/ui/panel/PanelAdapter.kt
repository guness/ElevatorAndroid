package com.guness.elevator.ui.panel

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
class PanelAdapter : RecyclerView.Adapter<PanelAdapter.PanelButtonHolder>() {

    private var entity: ElevatorEntity? = null
    var listener: FloorClickedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PanelButtonHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)
        return PanelButtonHolder(view)
    }

    override fun onBindViewHolder(holder: PanelButtonHolder, position: Int) {
        holder.bind(position + (entity?.minFloor ?: 0))
    }

    override fun getItemCount() = entity?.floorCount ?: 0


    fun setElevator(entity: ElevatorEntity?) {
        this.entity = entity
        notifyDataSetChanged()
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
            itemView.setTag(floor)
        }
    }

    interface FloorClickedListener {
        fun onClick(floor: Int)
    }
}