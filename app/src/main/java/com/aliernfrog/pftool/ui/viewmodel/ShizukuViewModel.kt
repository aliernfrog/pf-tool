package com.aliernfrog.pftool.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.TAG
import com.aliernfrog.pftool.enum.ShizukuStatus
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.ShizukuProvider
import rikka.shizuku.SystemServiceHelper

class ShizukuViewModel(
    context: Context
) : ViewModel() {
    companion object {
        const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
    }

    private val packageName = context.packageName

    var status by mutableStateOf(ShizukuStatus.UNKNOWN)

    val installed: Boolean
        get() = status != ShizukuStatus.NOT_INSTALLED && status != ShizukuStatus.UNKNOWN

    private val permissionResultListener = Shizuku.OnRequestPermissionResultListener { _ /* requestCode */, _ /*grantResult*/ ->
        checkAvailability(context)
    }

    var takePersistableUriPermission: ((Uri, Int) -> Unit)? = null
        private set

    init {
        // TODO do not run if no need?
        Shizuku.addRequestPermissionResultListener(permissionResultListener)
        checkAvailability(context)
    }

    fun checkAvailability(context: Context): ShizukuStatus {
        status = try {
            if (!isInstalled(context)) ShizukuStatus.NOT_INSTALLED
            else {
                val permission = if (Shizuku.isPreV11()) Shizuku.checkSelfPermission()
                else ContextCompat.checkSelfPermission(context, ShizukuProvider.PERMISSION)
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    if (takePersistableUriPermission == null) getUriGrantsManager()
                    ShizukuStatus.AVAILABLE
                }
                else ShizukuStatus.UNAUTHORIZED
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateStatus: ", e)
            ShizukuStatus.UNKNOWN
        }
        Log.d(TAG, "updateStatus: $status")
        Log.d(TAG, "updateStatus: installed = $installed")
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

    @SuppressLint("PrivateApi")
    fun getUriGrantsManager() {
        val uriGrantsManagerServiceClass = Class.forName("com.android.service.uri.UriGrantsManagerService")
        val uriGrantsManagerStub = Class.forName("android.app.IUriGrantsManager\$Stub")
        Log.d(TAG, uriGrantsManagerStub.methods   .joinToString(", ") { it.name })
        val asInterfaceMethod = uriGrantsManagerStub.getMethod("asInterface", IBinder::class.java)
        // takePersistableUriPermission(uri: Uri, modeFlags: Int, toPackage: String?, userId: Int)
        val takePersistableUriPermissionMethod = uriGrantsManagerServiceClass.getMethod("grantUriPermissionUnchecked", Uri::class.java, Int::class.java, String::class.java, Int::class.java)
        val uriGrantsManagerInstance = asInterfaceMethod.invoke(null, ShizukuBinderWrapper(SystemServiceHelper.getSystemService("uri_grants")))
        takePersistableUriPermission = { uri, takeFlags ->
            Log.d(TAG, "granting permission to $uri with Shizuku..")
            takePersistableUriPermissionMethod.invoke(uriGrantsManagerInstance, uri, takeFlags, packageName, 0)
        }
    }

    override fun onCleared() {
        Shizuku.removeRequestPermissionResultListener(permissionResultListener)
        super.onCleared()
    }
}