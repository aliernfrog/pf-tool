package com.aliernfrog.pftool.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.enum.FileManagementMethod
import com.aliernfrog.pftool.enum.SAFWorkaroundLevel
import com.aliernfrog.pftool.hasAndroidDataRestrictions
import com.aliernfrog.pftool.util.extension.appHasPermissions
import com.aliernfrog.pftool.util.manager.PreferenceManager

class PermissionsViewModel(
    val prefs: PreferenceManager
) : ViewModel() {
    var fileManagementMethod: FileManagementMethod
        get() = FileManagementMethod.entries[prefs.fileManagementMethod]
        set(value) { prefs.fileManagementMethod = value.ordinal }

    var safWorkaroundLevel by mutableStateOf(SAFWorkaroundLevel.entries.first())

    fun pushSAFWorkaroundLevel() {
        if (!hasAndroidDataRestrictions) return
        val newIndex = safWorkaroundLevel.ordinal+1
        SAFWorkaroundLevel.entries.let {
            if (newIndex > SAFWorkaroundLevel.UNINSTALL_FILES_APP_UPDATES.ordinal) fileManagementMethod = FileManagementMethod.SHIZUKU
            else safWorkaroundLevel = SAFWorkaroundLevel.entries[newIndex]
        }
    }

    fun hasPermissions(
        vararg permissionsData: PermissionData,
        isShizukuFileServiceRunning: Boolean,
        context: Context
    ): Boolean {
        return when (fileManagementMethod) {
            FileManagementMethod.SAF -> getMissingUriPermissions(
                *permissionsData, context = context
            ).isEmpty()
            FileManagementMethod.SHIZUKU -> isShizukuFileServiceRunning
        }
    }

    fun getMissingUriPermissions(
        vararg permissionsData: PermissionData,
        context: Context
    ): List<PermissionData> {
        return permissionsData.filter {
            !Uri.parse(it.getUri()).appHasPermissions(context)
        }
    }
}