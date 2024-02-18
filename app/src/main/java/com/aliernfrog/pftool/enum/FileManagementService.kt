package com.aliernfrog.pftool.enum

import androidx.annotation.StringRes
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.util.manager.PreferenceManager

enum class FileManagementService(
    @StringRes val label: Int,
    val isEnabled: (PreferenceManager) -> Boolean,
    val enable: (PreferenceManager) -> Unit
) {
    SAF(
        label = R.string.settings_general_fileManagementService_saf,
        isEnabled = {
            it.permissionsSetupGuideLevel < PermissionSetupGuideLevel.SETUP_SHIZUKU.ordinal
        },
        enable = {
            it.permissionsSetupGuideLevel = 0
        }
    ),

    SHIZUKU(
        label = R.string.settings_general_fileManagementService_shizuku,
        isEnabled = {
            it.permissionsSetupGuideLevel >= PermissionSetupGuideLevel.SETUP_SHIZUKU.ordinal
        },
        enable = {
            it.permissionsSetupGuideLevel = PermissionSetupGuideLevel.SETUP_SHIZUKU.ordinal
        }
    )
}