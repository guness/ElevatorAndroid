package com.guness.utils

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import timber.log.Timber

/**
 * Created by guness on 27.12.2017.
 */

/**
 * http://stackoverflow.com/a/38611931
 */
public class SGLinearLayoutManager : LinearLayoutManager {
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    constructor(context: Context) : super(context) {}

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout) {}

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: Exception) {
            Timber.w(e, "Exception in RecyclerView happens on onLayoutChildren")
        }
    }

    //http://blog.csdn.net/jiangxuelei_2015/article/details/69723716
    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        return try {
            super.scrollHorizontallyBy(dx, recycler, state)
        } catch (e: Exception) {
            Timber.w(e, "Exception in RecyclerView happens on scrollHorizontallyBy")
            0
        }
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        return try {
            super.scrollVerticallyBy(dy, recycler, state)
        } catch (e: Exception) {
            Timber.w(e, "Exception in RecyclerView happens on scrollHorizontallyBy")
            0
        }
    }
}