package com.guness.elevator.ui.pages.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.guness.core.SGActivity
import com.guness.elevator.R
import com.guness.elevator.db.ElevatorEntity
import com.guness.elevator.db.FavoriteEntity
import com.guness.elevator.db.GroupEntity
import com.guness.elevator.ui.pickers.elevator.ElevatorPickerFragment
import com.guness.elevator.ui.pickers.floor.FloorPickerFragment
import com.guness.elevator.ui.pickers.group.GroupPickerFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : SGActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, View.OnLongClickListener {

    private lateinit var mViewModel: MainViewModel

    override fun getViewModel() = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mViewModel.groups.observe(this, Observer { list ->
            val menu = nav_view.menu
            menu.clear()

            list?.forEach {
                val group = it.group!!
                val groupId = group.id.toInt()
                val subMenu = menu.addSubMenu(Menu.NONE, groupId, Menu.NONE, group.description)
                it.elevators?.forEach { elevator ->
                    subMenu.add(groupId, elevator.id.toInt(), Menu.NONE, elevator.description)
                            .setIcon(R.drawable.ic_elevator)
                }
            }
            menu.add(Menu.NONE, 0, Menu.FIRST, R.string.add_new)
                    .setIcon(R.drawable.ic_playlist_add)
                    .setOnMenuItemClickListener {
                        mViewModel.onAddElevatorClicked()
                    }
            menu.add(Menu.NONE, 0, Menu.FIRST + 1, R.string.delete_group)
                    .setIcon(R.drawable.ic_delete_sweep)
                    .setOnMenuItemClickListener {
                        mViewModel.onDeleteElevatorClicked()
                    }
        })
        mViewModel.showElevatorPickerCommand.observe(this, Observer {
            if (it != null) {
                val ft = supportFragmentManager.beginTransaction()
                val prev = supportFragmentManager.findFragmentByTag("elevator_picker")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)

                val newFragment = ElevatorPickerFragment.newInstance(it.second)
                newFragment.show(ft, "elevator_picker")
                newFragment.setListener(object : ElevatorPickerFragment.ElevatorPickerListener {
                    override fun onElevatorPicked(entity: ElevatorEntity) {
                        mViewModel.onFavoriteElevatorPicked(it.first, entity)
                    }

                    override fun onElevatorRemoved() {
                        mViewModel.onFavoriteRemoved(it.first)
                    }
                })
            }
        })
        mViewModel.showFloorPickerCommand.observe(this, Observer {
            if (it != null) {
                val ft = supportFragmentManager.beginTransaction()
                val prev = supportFragmentManager.findFragmentByTag("floor_picker")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)

                val newFragment = FloorPickerFragment.newInstance(it.second.device, it.third)
                newFragment.show(ft, "floor_picker")
                newFragment.setListener(object : FloorPickerFragment.FloorPickerListener {
                    override fun onFloorPicked(floor: Int) {
                        mViewModel.onFavoriteFloorPicked(it.first, it.second, floor)
                    }

                    override fun onFloorRemoved() {
                        mViewModel.onFavoriteRemoved(it.first)
                    }
                })
            }
        })
        mViewModel.showGroupPickerCommand.observe(this, Observer {
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("group_picker")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)

            val newFragment = GroupPickerFragment.newInstance()
            newFragment.show(ft, "group_picker")
            newFragment.setListener(object : GroupPickerFragment.GroupPickerListener {
                override fun onGroupRemoved(entity: GroupEntity) {
                    mViewModel.onGroupRemoved(entity)
                }
            })
        })
        mViewModel.favorites.observe(this, Observer { list ->
            label1.setText(R.string.click_to_add_elevator)
            label3.setText(R.string.click_to_add_elevator)
            label5.setText(R.string.click_to_add_elevator)
            label7.setText(R.string.click_to_add_elevator)

            label2.setText(R.string.click_to_add_floor)
            label4.setText(R.string.click_to_add_floor)
            label6.setText(R.string.click_to_add_floor)
            label8.setText(R.string.click_to_add_floor)

            button2b.setText(R.string.plus_sign)
            button4b.setText(R.string.plus_sign)
            button6b.setText(R.string.plus_sign)
            button8b.setText(R.string.plus_sign)

            list?.forEach {
                when (it.key) {
                    FavoriteEntity.L1 -> label1.text = it.description
                    FavoriteEntity.L2 -> label3.text = it.description
                    FavoriteEntity.L3 -> label5.text = it.description
                    FavoriteEntity.L4 -> label7.text = it.description
                    FavoriteEntity.R1 -> {
                        label2.text = it.description
                        button2b.text = it.floor.toString()
                    }
                    FavoriteEntity.R2 -> {
                        label4.text = it.description
                        button4b.text = it.floor.toString()
                    }
                    FavoriteEntity.R3 -> {
                        label6.text = it.description
                        button6b.text = it.floor.toString()
                    }
                    FavoriteEntity.R4 -> {
                        label8.text = it.description
                        button8b.text = it.floor.toString()
                    }
                }
            }
        })
        button1b.setOnClickListener(this)
        button2b.setOnClickListener(this)
        button3b.setOnClickListener(this)
        button4b.setOnClickListener(this)
        button5b.setOnClickListener(this)
        button6b.setOnClickListener(this)
        button7b.setOnClickListener(this)
        button8b.setOnClickListener(this)

        button1b.setOnLongClickListener(this)
        button2b.setOnLongClickListener(this)
        button3b.setOnLongClickListener(this)
        button4b.setOnLongClickListener(this)
        button5b.setOnLongClickListener(this)
        button6b.setOnLongClickListener(this)
        button7b.setOnLongClickListener(this)
        button8b.setOnLongClickListener(this)
    }

    override fun onClick(v: View) {
        mViewModel.onFavoriteClicked(when (v) {
            button1b -> FavoriteEntity.L1
            button3b -> FavoriteEntity.L2
            button5b -> FavoriteEntity.L3
            button7b -> FavoriteEntity.L4
            button2b -> FavoriteEntity.R1
            button4b -> FavoriteEntity.R2
            button6b -> FavoriteEntity.R3
            button8b -> FavoriteEntity.R4
            else -> throw UnsupportedOperationException("This type is not supported")
        })
    }

    override fun onLongClick(v: View): Boolean {
        mViewModel.onFavoriteLongClicked(when (v) {
            button1b -> FavoriteEntity.L1
            button3b -> FavoriteEntity.L2
            button5b -> FavoriteEntity.L3
            button7b -> FavoriteEntity.L4
            button2b -> FavoriteEntity.R1
            button4b -> FavoriteEntity.R2
            button6b -> FavoriteEntity.R3
            button8b -> FavoriteEntity.R4
            else -> throw UnsupportedOperationException("This type is not supported")
        })
        return true
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        if (item.itemId > 0) {
            mViewModel.onElevatorSelected(item.groupId, item.itemId)
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
