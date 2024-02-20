package com.aliernfrog.pftool.enum

import androidx.annotation.StringRes
import com.aliernfrog.pftool.ConfigKey
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.pftool.util.staticutil.FileUtil

enum class FileManagementMethod(
    @StringRes val label: Int,
    val enable: (PreferenceManager) -> Unit
) {
    SAF(
        label = R.string.settings_general_fileManagementService_saf,
        enable = {
            it.fileManagementMethod = SAF.ordinal
            it.pfMapsDir = FileUtil.getTreeUriForPath(it.pfMapsDir).toString()
            it.exportedMapsDir = FileUtil.getTreeUriForPath(it.exportedMapsDir).toString()
        }
    ),

    SHIZUKU(
        label = R.string.settings_general_fileManagementService_shizuku,
        enable = {
            it.fileManagementMethod = SHIZUKU.ordinal
            it.pfMapsDir = FileUtil.getFilePath(it.pfMapsDir) ?: ConfigKey.RECOMMENDED_MAPS_DIR
            it.exportedMapsDir = FileUtil.getFilePath(it.exportedMapsDir) ?: ConfigKey.RECOMMENDED_EXPORTED_MAPS_DIR
        }
    )
}