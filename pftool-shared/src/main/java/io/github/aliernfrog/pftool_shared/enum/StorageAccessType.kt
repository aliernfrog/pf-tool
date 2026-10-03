package io.github.aliernfrog.pftool_shared.enum

import android.os.Build
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import kotlin.reflect.KProperty1

enum class StorageAccessType(
    val label: KProperty1<PFToolSharedString, Int>,
    val description: KProperty1<PFToolSharedString, Int>,
    val isCompatible: () -> Boolean
) {
    SAF(
        label = PFToolSharedString::settingsStorageStorageAccessTypeSAF,
        description = PFToolSharedString::settingsStorageStorageAccessTypeSAFDescription,
        isCompatible = { true }
    ),

    SHIZUKU(
        label = PFToolSharedString::settingsStorageStorageAccessTypeShizuku,
        description = PFToolSharedString::settingsStorageStorageAccessTypeShizukuDescription,
        isCompatible = {
            true
            //Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        }
    ),

    ALL_FILES(
        label = PFToolSharedString::settingsStorageStorageAccessTypeAllFiles,
        description = PFToolSharedString::settingsStorageStorageAccessTypeAllFilesDescription,
        isCompatible = {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1
        }
    )
}