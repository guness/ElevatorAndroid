package com.guness.core

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.annotation.CallSuper
import android.widget.Toast
import com.guness.BackgroundService
import com.guness.BackgroundService.LocalBinder
import com.guness.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by guness on 9.12.2017.
 */
abstract class SGViewModel(application: Application) : AndroidViewModel(application), ServiceConnection {

    private val mCompositeDisposable = CompositeDisposable()
    val launchCommand: SingleLiveEvent<Pair<Boolean, Intent?>> = SingleLiveEvent()

    // Any viewModel can show a progress bar or progress dialog if Views support.
    protected var mProgressBarVisibility = MutableLiveData<Boolean>()
    protected var mProgressDialogVisibility = MutableLiveData<Boolean>()

    @SuppressLint("StaticFieldLeak")
    protected var service: BackgroundService? = null
    private var mServiceBound = false

    override fun onServiceConnected(className: ComponentName, binder: IBinder) {
        // We've bound to LocalService, cast the IBinder and get LocalService instance
        service = (binder as LocalBinder).service
        mServiceBound = true
    }

    override fun onServiceDisconnected(arg0: ComponentName) {
        mServiceBound = false
    }

    init {
        mProgressBarVisibility.postValue(false)
        mProgressDialogVisibility.postValue(false)
    }

    @CallSuper
    open fun onStart() {
        val intent = Intent(getAppContext(), BackgroundService::class.java)
        getApp().bindService(intent, this, Context.BIND_AUTO_CREATE)
    }


    @CallSuper
    open fun onStop() {
    }

    // Unsubscribe Rx observables when we are done with the ViewModel
    @CallSuper
    override fun onCleared() {
        mCompositeDisposable.clear()
        getApp().unbindService(this)
        mServiceBound = false
    }

    protected fun subscribeUntilDetach(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    val progressBarVisibility: LiveData<Boolean>
        get() = mProgressBarVisibility

    val progressDialogVisibility: LiveData<Boolean>
        get() = mProgressDialogVisibility

    fun showToast(definition: Int) {
        Toast.makeText(getApplication(), definition, Toast.LENGTH_LONG).show()
    }

    fun showToast(definition: String) {
        Toast.makeText(getApplication(), definition, Toast.LENGTH_LONG).show()
    }

    fun getAppContext(): Context {
        return getApp()
    }

    protected fun getApp(): SGApplication {
        return getApplication()
    }
}
