package com.guness.core

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.support.annotation.CallSuper
import android.widget.Toast
import com.guness.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by guness on 9.12.2017.
 */
abstract class SGViewModel(application: Application) : AndroidViewModel(application) {

    private val mCompositeDisposable = CompositeDisposable()
    val launchCommand: SingleLiveEvent<Pair<Boolean, Intent?>> = SingleLiveEvent()

    // Any viewModel can show a progress bar or progress dialog if Views support.
    protected var mProgressBarVisibility = MutableLiveData<Boolean>()
    protected var mProgressDialogVisibility = MutableLiveData<Boolean>()

    init {
        mProgressBarVisibility.postValue(false)
        mProgressDialogVisibility.postValue(false)
    }

    @CallSuper
    open fun onStart() {
    }

    @CallSuper
    open fun onStop() {
    }

    // Unsubscribe Rx observables when we are done with the ViewModel
    @CallSuper
    override fun onCleared() {
        mCompositeDisposable.clear()
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

    fun getApp(): SGApplication {
        return getApplication()
    }
}
