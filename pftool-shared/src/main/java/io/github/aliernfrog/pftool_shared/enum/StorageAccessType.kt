package io.github.aliernfrog.pftool_shared.enum

import android.os.Build
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.shared.util.SharedString

enum class StorageAccessType(
    val label: SharedString,
    val description: SharedString,
    val isCompatible: () -> Boolean
) {
    SAF(
        label = PFToolSharedString.SettingsStorageStorageAccessTypeSAF,
        description = PFToolSharedString.SettingsStorageStorageAccessTypeSAFDescription,
        isCompatible = { true }
    ),

    SHIZUKU(
        label = PFToolSharedString.SettingsStorageStorageAccessTypeShizuku,
        description = PFToolSharedString.SettingsStorageStorageAccessTypeShizukuDescription,
        isCompatible = {
            true
            //Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        }
    ),

    ALL_FILES(
        label = PFToolSharedString.SettingsStorageStorageAccessTypeAllFiles,
        description = PFToolSharedString.SettingsStorageStorageAccessTypeAllFilesDescription,
        isCompatible = {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1
        }
    )
}