package com.aliernfrog.pftool.enum

import androidx.annotation.StringRes
import com.aliernfrog.pftool.ConfigKey
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.pftool.util.staticutil.FileUtil

enum class StorageAccessType(
    @StringRes val label: Int,
    @StringRes val description: Int,
    val enable: (PreferenceManager) -> Unit
) {
    SAF(
        label = R.string.settings_storage_storageAccessType_saf,
        description = R.string.settings_storage_storageAccessType_saf_description,
        enable = {
            it.storageAccessType = SAF.ordinal
            it.pfMapsDir = FileUtil.getTreeUriForPath(it.pfMapsDir).toString()
            it.exportedMapsDir = FileUtil.getTreeUriForPath(it.exportedMapsDir).toString()
        }
    ),

    SHIZUKU(
        label = R.string.settings_storage_storageAccessType_shizuku,
        description = R.string.settings_storage_storageAccessType_shizuku_description,
        enable = {
            it.storageAccessType = SHIZUKU.ordinal
            it.pfMapsDir = FileUtil.getFilePath(it.pfMapsDir) ?: ConfigKey.RECOMMENDED_MAPS_DIR
            it.exportedMapsDir = FileUtil.getFilePath(it.exportedMapsDir) ?: ConfigKey.RECOMMENDED_EXPORTED_MAPS_DIR
        }
    )
}