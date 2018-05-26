package com.guness.elevator.ui.pages.info

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.guness.core.SGActivity
import com.guness.elevator.R
import com.guness.elevator.model.Group
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : SGActivity() {

    private lateinit var mViewModel: InfoViewModel

    override fun getViewModel() = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mViewModel = ViewModelProviders.of(this).get(InfoViewModel::class.java)
        mViewModel.setLink(intent.getParcelableExtra(EXTRA_LINK))

        mViewModel.group.observe(this, Observer<Group> {
            description.text = it?.description
            address.text = it?.address
            numElevators.text = (it?.elevators?.size ?: 0).toString()
        })
        button.setOnClickListener { onBackPressed() }
    }

    companion object {
        private val EXTRA_LINK = "EXTRA_LINK"

        fun newIntent(context: Context, link: Uri): Intent {
            return Intent(context, InfoActivity::class.java)
                    .putExtra(EXTRA_LINK, link)
        }
    }
}
