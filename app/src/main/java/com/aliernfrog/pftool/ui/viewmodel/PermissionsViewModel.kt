package com.aliernfrog.pftool.ui.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.enum.StorageAccessType
import com.aliernfrog.pftool.util.extension.appHasPermissions
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState

class PermissionsViewModel(
    val topToastState: TopToastState,
    val prefs: PreferenceManager
) : ViewModel() {
    private var storageAccessType: StorageAccessType
        get() = StorageAccessType.entries[prefs.storageAccessType.value]
        set(value) { value.enable(prefs) }

    var showShizukuIntroDialog by mutableStateOf(false)
    var showFilesDowngradeDialog by mutableStateOf(false)

    fun hasPermissions(
        vararg permissionsData: PermissionData,
        isShizukuFileServiceRunning: Boolean,
        context: Context
    ): Boolean {
        return when (storageAccessType) {
            StorageAccessType.SAF -> getMissingUriPermissions(
                *permissionsData, context = context
            ).isEmpty()
            StorageAccessType.SHIZUKU -> isShizukuFileServiceRunning
            StorageAccessType.ALL_FILES -> {
                val result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                result == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    fun getMissingUriPermissions(
        vararg permissionsData: PermissionData,
        context: Context
    ): List<PermissionData> {
        return permissionsData.filter {
            !Uri.parse(it.pref.value).appHasPermissions(context)
        }
    }
}