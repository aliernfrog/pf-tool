package com.aliernfrog.pftool.enum

import android.os.Build
import androidx.annotation.StringRes
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.pftool.util.staticutil.FileUtil

enum class StorageAccessType(
    @StringRes val label: Int,
    @StringRes val description: Int,
    val minSDK: Int = 0,
    val maxSDK: Int = Integer.MAX_VALUE,
    val enable: (PreferenceManager) -> Unit
) {
    SAF(
        label = R.string.settings_storage_storageAccessType_saf,
        description = R.string.settings_storage_storageAccessType_saf_description,
        enable = {
            it.storageAccessType.value = SAF.ordinal
            it.pfMapsDir.value = FileUtil.getTreeUriForPath(it.pfMapsDir.value).toString()
            it.exportedMapsDir.value = FileUtil.getTreeUriForPath(it.exportedMapsDir.value).toString()
        }
    ),

    SHIZUKU(
        label = R.string.settings_storage_storageAccessType_shizuku,
        description = R.string.settings_storage_storageAccessType_shizuku_description,
        minSDK = Build.VERSION_CODES.M,
        enable = {
            it.storageAccessType.value = SHIZUKU.ordinal
            it.pfMapsDir.value = FileUtil.getFilePath(it.pfMapsDir.value)
            it.exportedMapsDir.value = FileUtil.getFilePath(it.exportedMapsDir.value)
        }
    ),

    ALL_FILES(
        label = R.string.settings_storage_storageAccessType_allFiles,
        description = R.string.settings_storage_storageAccessType_allFiles_description,
        maxSDK = Build.VERSION_CODES.N_MR1,
        enable = {
            it.storageAccessType.value = ALL_FILES.ordinal
            it.pfMapsDir.value = FileUtil.getFilePath(it.pfMapsDir.value)
            it.exportedMapsDir.value = FileUtil.getFilePath(it.exportedMapsDir.value)
        }
    )
}

fun StorageAccessType.isCompatible(): Boolean {
    val sdk = Build.VERSION.SDK_INT
    return sdk in minSDK..maxSDK
}