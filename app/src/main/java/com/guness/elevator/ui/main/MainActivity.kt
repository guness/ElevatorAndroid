package com.guness.elevator.ui.main

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
import com.guness.core.SGActivity
import com.guness.elevator.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : SGActivity(), NavigationView.OnNavigationItemSelectedListener {

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
