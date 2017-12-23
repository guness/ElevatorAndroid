package com.guness.core

import android.Manifest
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.guness.utils.DialogHelper
import timber.log.Timber

/**
 * Created by guness on 9.12.2017.
 */
abstract class SGActivity : AppCompatActivity() {

    private var mSGViewModel: SGViewModel? = null
    private var mDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //performPermissionCheck()
    }

    private fun performPermissionCheck() {
        if (!checkPermissionForReadExternalStorage() || !checkPermissionForLocation()) {
            try {
                requestPermissions()
            } catch (e: Exception) {
                Timber.e(e)
            }
        } else {
            onPermissionsGranted()
        }
    }

    private fun onPermissionsGranted() {
        //TODO: maybe ward service
    }

    override fun onStart() {
        super.onStart()
        mSGViewModel = getViewModel()
        if (mSGViewModel != null) {
            mSGViewModel!!.progressDialogVisibility.observe(this, Observer {
                if (it == true) {
                    showDialog()
                } else {
                    hideDialog()
                }
            })
            mSGViewModel!!.launchCommand.observe(this, Observer {
                if (it != null) {
                    if (it.second != null) {
                        startActivity(it.second)
                    }
                    if (it.first) {
                        finish()
                    }
                }
            })
            mSGViewModel!!.onStart()
        }
    }

    private fun hideDialog() {
        mDialog?.dismiss()
    }

    private fun showDialog() {
        if (mDialog == null) {
            mDialog = DialogHelper.makeDialog(this)
        }
        mDialog!!.show()
    }

    /**
     * @param activity to launch with no extras or flags.
     */
    protected fun startActivity(activity: Class<out SGActivity>) {
        startActivity(Intent(this, activity))
    }

    /**
     * Used for finishing activities nice
     */
    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkPermissionForReadExternalStorage(): Boolean {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            return true
        }
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissionForLocation(): Boolean {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            PackageManager.PERMISSION_GRANTED
        }
        return result == PackageManager.PERMISSION_GRANTED
    }

    @Throws(Exception::class)
    private fun requestPermissions() {
        try {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                performPermissionCheck()
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1010
    }

    protected abstract fun getViewModel(): SGViewModel?
}