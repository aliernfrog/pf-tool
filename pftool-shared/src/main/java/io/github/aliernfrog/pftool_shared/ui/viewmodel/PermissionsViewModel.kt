package io.github.aliernfrog.pftool_shared.ui.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.pftool_shared.data.PermissionData
import androidx.core.net.toUri
import io.github.aliernfrog.pftool_shared.enum.StorageAccessType
import io.github.aliernfrog.pftool_shared.impl.ShizukuManager
import io.github.aliernfrog.pftool_shared.util.extension.appHasPermissions
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager

internal class PermissionsViewModel(
    private val storageAccessTypePref: () -> BasePreferenceManager.Preference<Int>,
    private val ignoreDocumentsUIRestrictionsPref: () -> BasePreferenceManager.Preference<Boolean>,
    private val onSetStorageAccessType: (StorageAccessType) -> Unit,
    val topToastState: TopToastState,
    val shizukuManager: ShizukuManager,
    context: Context
) : ViewModel() {
    val ignoreDocumentsUIRestrictions: Boolean
        get() = ignoreDocumentsUIRestrictionsPref().value

    var storageAccessType: StorageAccessType
        get() = StorageAccessType.entries[storageAccessTypePref().value]
        set(value) { onSetStorageAccessType(value) }

    val currentShizukuVersion = shizukuManager.getCurrentShizukuVersionNameSimplified(context) ?: "unknown"
    val isShizukuFileServiceRunning
        get() = shizukuManager.fileServiceRunning

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
                val result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                result == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    fun getMissingUriPermissions(
        vararg permissionsData: PermissionData,
        context: Context
    ): List<PermissionData> {
        return permissionsData.filter {
            !it.pref.value.toUri().appHasPermissions(context)
        }
    }
}