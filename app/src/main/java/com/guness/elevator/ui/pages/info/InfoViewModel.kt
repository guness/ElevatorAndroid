package com.guness.elevator.ui.pages.info

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.net.Uri
import android.os.IBinder
import com.guness.core.SGViewModel
import com.guness.elevator.model.Group
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.util.*

/**
 * Created by guness on 23.12.2017.
 */
class InfoViewModel(application: Application) : SGViewModel(application) {

    val group: MutableLiveData<Group> = MutableLiveData()

    private var mUUID: BehaviorSubject<UUID> = BehaviorSubject.create()

    override fun onStart() {
        super.onStart()
        //launchCommand.value = Pair(true, MainActivity.newIntent(getAppContext()))
    }

    fun setLink(uri: Uri) {
        val uuid = uri.getQueryParameter("uuid")
        if (uuid == null) {
            Timber.e("Cannot find parameter on the uri: $uri")
        } else {
            try {
                mUUID.onNext(UUID.fromString(uuid))
            } catch (e: IllegalArgumentException) {
                Timber.e(e, "Cannot parse UUID on the uri: $uri")
            }
        }
    }


    override fun onServiceConnected(className: ComponentName, binder: IBinder) {
        super.onServiceConnected(className, binder)

        mUUID.take(1)
                .subscribe { uuid ->

                    service!!.groupInfoObservable
                            .filter { it.group?.uuid == uuid.toString() }
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                group.value = it.group
                            }

                    service!!.fetchUUID(uuid)
                }
    }
}