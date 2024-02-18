package com.aliernfrog.pftool.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.BuildConfig
import com.aliernfrog.pftool.IFileService
import com.aliernfrog.pftool.TAG
import com.aliernfrog.pftool.enum.ShizukuStatus
import com.aliernfrog.pftool.impl.FileService
import com.aliernfrog.toptoast.state.TopToastState
import rikka.shizuku.Shizuku


class ShizukuViewModel(
    context: Context,
    val topToastState: TopToastState
) : ViewModel() {
    companion object {
        const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
    }

    var status by mutableStateOf(ShizukuStatus.UNKNOWN)
    val installed: Boolean
        get() = status != ShizukuStatus.NOT_INSTALLED && status != ShizukuStatus.UNKNOWN

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        checkAvailability(context)
    }
    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        checkAvailability(context)
    }
    private val permissionResultListener = Shizuku.OnRequestPermissionResultListener { _ /* requestCode */, _ /*grantResult*/ ->
        checkAvailability(context)
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            Log.d(TAG, "user service connected")
            val service = IFileService.Stub.asInterface(binder)
            val files = service.listFiles(Environment.getExternalStorageDirectory().toString()+"/Android/data/com.MA.Polyfield/files")
            topToastState.showToast(files.joinToString("\n"))
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d(TAG, "user service disconnected")
        }
    }

    private val userServiceArgs = Shizuku.UserServiceArgs(ComponentName(BuildConfig.APPLICATION_ID, FileService::class.java.name))
        .processNameSuffix("service")
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)


    init {
        Shizuku.addBinderReceivedListener(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
        Shizuku.addRequestPermissionResultListener(permissionResultListener)
    }

    fun checkAvailability(context: Context): ShizukuStatus {
        status = try {
            if (!isInstalled(context)) ShizukuStatus.NOT_INSTALLED
            else if (!Shizuku.pingBinder()) ShizukuStatus.WAITING_FOR_BINDER
            else {
                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) ShizukuStatus.AVAILABLE
                else ShizukuStatus.UNAUTHORIZED
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateStatus: ", e)
            ShizukuStatus.UNKNOWN
        }
        if (status == ShizukuStatus.AVAILABLE) Shizuku.bindUserService(userServiceArgs, userServiceConnection)
        return status
    }

    fun isInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0) != null
        } catch (e: Exception) {
            Log.e(TAG, "isInstalled: ", e)
            false
        }
    }

    override fun onCleared() {
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
        Shizuku.removeRequestPermissionResultListener(permissionResultListener)
        super.onCleared()
    }
}