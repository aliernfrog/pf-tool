package com.aliernfrog.pftool.enum

import androidx.annotation.StringRes
import com.aliernfrog.pftool.R

enum class FileManagementMethod(
    @StringRes val label: Int
) {
    SAF(
        label = R.string.settings_general_fileManagementService_saf
    ),

    SHIZUKU(
        label = R.string.settings_general_fileManagementService_shizuku
    )
}