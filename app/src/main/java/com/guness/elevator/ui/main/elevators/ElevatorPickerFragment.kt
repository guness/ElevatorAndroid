package com.guness.elevator.ui.main.elevators


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.guness.elevator.R
import com.guness.elevator.db.ElevatorEntity
import com.guness.elevator.db.FavoriteEntity.TypeDef
import com.guness.elevator.ui.ElevatorAdapter
import com.guness.elevator.ui.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_elevators.*


/**
 * A simple [Fragment] subclass.
 */
class ElevatorPickerFragment : DialogFragment() {
    private lateinit var mAdapter: ElevatorAdapter
    private lateinit var mViewModel: ElevatorPickerViewModel
    private lateinit var mMainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(ElevatorPickerViewModel::class.java)
        mMainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        mViewModel.key = arguments?.getString(EXTRA_KEY)
        mAdapter = ElevatorAdapter()
        mAdapter.preselected = arguments?.getString(EXTRA_PICKED)
        mViewModel.groups.observe(this, Observer {
            mAdapter.setGroups(it)
        })
        mViewModel.elevators.observe(this, Observer {
            mAdapter.setList(it)
        })
        mAdapter.listener = object : ElevatorAdapter.OnElevatorClickedListener {
            override fun onElevatorClicked(entity: ElevatorEntity) {
                mMainViewModel.onFavoriteElevatorPicked(mViewModel.key!!, entity)
                dismiss()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_elevators, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.adapter = mAdapter
        listView.addItemDecoration(object : RecyclerView.ItemDecoration() {

            private val textSize = 50
            private val groupSpacing = 100
            private val itemsInGroup = 3

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
                if (parent.getChildAdapterPosition(view) % itemsInGroup == 0) {
                    outRect.set(0, groupSpacing, 0, 0)
                }
            }
        })
        removeButton.setOnClickListener {
            mMainViewModel.onFavoriteElevatorRemoved(mViewModel.key!!)
            dismiss()
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private val EXTRA_KEY = "EXTRA_KEY"
        private val EXTRA_PICKED = "EXTRA_PICKED"
        fun newInstance(@TypeDef key: String, device: String?): ElevatorPickerFragment {
            val fragment = ElevatorPickerFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_KEY, key)
            if (device != null) {
                bundle.putString(EXTRA_PICKED, device)
            }
            fragment.arguments = Bundle(bundle)
            return fragment
        }
    }

}// Required empty public constructor
