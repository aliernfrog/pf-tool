package io.github.aliernfrog.pftool_shared.impl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.core.net.toUri
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.pftool_shared.IFileService
import io.github.aliernfrog.pftool_shared.enum.ShizukuStatus
import io.github.aliernfrog.pftool_shared.service.FileService
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.TAG
import io.github.aliernfrog.shared.util.extension.showErrorToast
import io.github.aliernfrog.shared.util.getSharedString
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import rikka.shizuku.Shizuku
import java.io.File
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule

class ShizukuManager(
    applicationId: String,
    isDebugBuild: Boolean,
    private val shizukuNeverLoadPref: () -> BasePreferenceManager.Preference<Boolean>,
    private val topToastState: TopToastState,
    context: Context
) {
    companion object {
        const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
        const val SHIZUKU_PROBLEMATIC_VERSION_CODE = 1086.toLong()
        const val SHIZUKU_RECOMMENDED_VERSION_NAME = "v13.5.4"
        const val SHIZUKU_RECOMMENDED_VERSION_DOWNLOAD_URL = "https://github.com/RikkaApps/Shizuku/releases/download/v13.5.4/shizuku-v13.5.4.r1049.0e53409-release.apk"
        const val SHIZUKU_DOWNLOAD_URL = //"https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api"
            SHIZUKU_RECOMMENDED_VERSION_DOWNLOAD_URL
        const val SHIZUKU_RELEASES_URL = "https://github.com/RikkaApps/Shizuku/releases"
        const val SUI_GITHUB = "https://github.com/RikkaApps/Sui"
    }

    val deviceRooted = System.getenv("PATH")?.split(":")?.any { path ->
        File(path, "su").canExecute()
    } ?: false

    private val _status = MutableStateFlow(ShizukuStatus.UNKNOWN)
    val status = _status.asStateFlow()
    val shizukuInstalled: Boolean
        get() = !listOf(ShizukuStatus.NOT_INSTALLED, ShizukuStatus.UNKNOWN).any {
            _status.value == it
        }

    var fileService: IFileService? = null
        private set

    private val _fileServiceRunning = MutableStateFlow(false)
    val fileServiceRunning = _fileServiceRunning.asStateFlow()

    private val _shizukuVersionProblematic = MutableStateFlow(false)
    val shizukuVersionProblematic = _shizukuVersionProblematic.asStateFlow()

    private val _timedOut = MutableStateFlow(false)
    val timedOut = _timedOut.asStateFlow()

    private var timeOutTask: TimerTask? = null
    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        checkAvailability(context)
    }
    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        _fileServiceRunning.value = false
        checkAvailability(context)
    }
    private val permissionResultListener = Shizuku.OnRequestPermissionResultListener { _ /* requestCode */, _ /*grantResult*/ ->
        checkAvailability(context)
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            val shizukuNeverLoad = shizukuNeverLoadPref().value
            Log.d(TAG, "user service connected, shizukuNeverLoad: $shizukuNeverLoad")
            if (shizukuNeverLoad) return
            fileService = IFileService.Stub.asInterface(binder)
            _fileServiceRunning.value = true
            timeOutTask?.cancel()
            timeOutTask = null
            _timedOut.value = false
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d(TAG, "user service disconnected")
            _fileServiceRunning.value = false
            topToastState.showToast(
                text = context.getSharedString(PFToolSharedString.InfoShizukuDisconnected),
                icon = Icons.Default.Info
            )
        }
    }

    private val userServiceArgs = Shizuku.UserServiceArgs(
        ComponentName(applicationId, FileService::class.java.name)
    )
        .daemon(false)
        .processNameSuffix("service")
        .debuggable(isDebugBuild)
        .version(1)


    init {
        Shizuku.addBinderReceivedListener(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
        Shizuku.addRequestPermissionResultListener(permissionResultListener)
        checkAvailability(context)
    }

    fun launchShizuku(context: Context) {
        try {
            if (shizukuInstalled) context.startActivity(
                context.packageManager.getLaunchIntentForPackage(SHIZUKU_PACKAGE)
            ) else {
                val intent = Intent(Intent.ACTION_VIEW, SHIZUKU_DOWNLOAD_URL.toUri())
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "ShizukuManager/launchShizuku: failed to start activity ", e)
            topToastState.showErrorToast(
                text = context.getSharedString(SharedString.WarningError)
            )
        }
    }

    fun checkAvailability(context: Context): ShizukuStatus {
        _shizukuVersionProblematic.value = isShizukuVersionProblematic(context)
        _status.value = try {
            if (Shizuku.pingBinder()) {
                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) ShizukuStatus.AVAILABLE
                else ShizukuStatus.UNAUTHORIZED
            } else if (!isShizukuInstalled(context)) ShizukuStatus.NOT_INSTALLED
            else ShizukuStatus.WAITING_FOR_BINDER
        } catch (e: Exception) {
            Log.e(TAG, "ShizukuManager/checkAvailability: failed to determine status", e)
            ShizukuStatus.UNKNOWN
        }
        if (_status.value == ShizukuStatus.AVAILABLE && !_fileServiceRunning.value) {
            if (timeOutTask == null) timeOutTask = Timer().schedule(8000) {
                _timedOut.value = true
            }
            Shizuku.bindUserService(userServiceArgs, userServiceConnection)
        }
        return _status.value
    }

    fun getCurrentShizukuVersionNameSimplified(context: Context): String = "v" + getShizukuPackageInfo(context)?.versionName?.split(".")?.let {
        if (it.size > 3) it.take(3)
        else it
    }?.joinToString(".")

    fun disableShizukuNeverLoadPref() {
        shizukuNeverLoadPref().value = false
    }

    private fun isShizukuInstalled(context: Context) = getShizukuPackageInfo(context) != null

    private fun isShizukuVersionProblematic(context: Context) = getShizukuPackageInfo(context)?.let { packageInfo ->
        @Suppress("DEPRECATION")
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else packageInfo.versionCode.toLong()
        versionCode == SHIZUKU_PROBLEMATIC_VERSION_CODE
    } ?: false

    private fun getShizukuPackageInfo(context: Context): PackageInfo? = try {
        context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0)
    } catch (_: Exception) {
        null
    }

    /*override fun onCleared() {
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
        Shizuku.removeRequestPermissionResultListener(permissionResultListener)
        super.onCleared()
    }*/
}