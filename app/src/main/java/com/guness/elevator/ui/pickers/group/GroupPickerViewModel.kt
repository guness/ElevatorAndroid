package com.guness.elevator.ui.pickers.group

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.guness.core.SGApplication
import com.guness.elevator.db.GroupEntity

/**
 * Created by guness on 13.01.2018.
 */
class GroupPickerViewModel(application: Application) : AndroidViewModel(application) {
    val groups = getApplication<SGApplication>().getDatabase().dao().getGroupsLive()

    var selected: GroupEntity? = null
}