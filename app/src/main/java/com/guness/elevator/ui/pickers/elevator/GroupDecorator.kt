package com.guness.elevator.ui.pickers.elevator

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.guness.elevator.ui.adapters.ElevatorAdapter

/**
 * Created by guness on 13.01.2018.
 */
class GroupDecorator(private val mAdapter: ElevatorAdapter) : RecyclerView.ItemDecoration() {

    private val textSize = 50
    private val groupSpacing = 100

    private val paint = Paint()

    init {
        paint.textSize = textSize.toFloat()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val childView = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(childView)

            if (mAdapter.isFirstAtGroup(position)) {
                val title = mAdapter.getGroupTitle(position)
                if (title != null) {
                    c.drawText(title, childView.left.toFloat(), (childView.top - groupSpacing / 2 + textSize / 3).toFloat(), paint)
                }
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (mAdapter.isFirstAtGroup(position)) {
            outRect.set(0, groupSpacing, 0, 0)
        }
    }
}