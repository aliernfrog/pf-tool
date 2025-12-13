package io.github.aliernfrog.pftool_shared.enum

import android.os.Build
import io.github.aliernfrog.pftool_shared.util.SharedString

enum class StorageAccessType(
    val label: SharedString,
    val description: SharedString,
    val isCompatible: () -> Boolean
) {
    SAF(
        label = SharedString.SETTINGS_STORAGE_STORAGE_ACCESS_TYPE_SAF,
        description = SharedString.SETTINGS_STORAGE_STORAGE_ACCESS_TYPE_SAF_DESCRIPTION,
        isCompatible = { true }
    ),

    SHIZUKU(
        label = SharedString.SETTINGS_STORAGE_STORAGE_ACCESS_TYPE_SHIZUKU,
        description = SharedString.SETTINGS_STORAGE_STORAGE_ACCESS_TYPE_SHIZUKU_DESCRIPTION,
        isCompatible = {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        }
    ),

    ALL_FILES(
        label = SharedString.SETTINGS_STORAGE_STORAGE_ACCESS_TYPE_ALL_FILES,
        description = SharedString.SETTINGS_STORAGE_STORAGE_ACCESS_TYPE_ALL_FILES_DESCRIPTION,
        isCompatible = {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1
        }
    )
}