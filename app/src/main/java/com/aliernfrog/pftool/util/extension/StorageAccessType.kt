package com.aliernfrog.pftool.util.extension

import com.aliernfrog.pftool.util.manager.PreferenceManager
import io.github.aliernfrog.pftool_shared.enum.StorageAccessType
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.di.getKoinInstance

fun StorageAccessType.enable() {
    val prefs = getKoinInstance<PreferenceManager>()
    val prefsToUpdate = listOf(
        prefs.pfMapsDir, prefs.exportedMapsDir
    )

    prefs.storageAccessType.value = this.ordinal
    prefsToUpdate.forEach { pref ->
        pref.value = when (this) {
            StorageAccessType.SAF -> PFToolSharedUtil.getTreeUriForPath(pref.value).toString()
            StorageAccessType.SHIZUKU, StorageAccessType.ALL_FILES -> PFToolSharedUtil.getFilePath(pref.value)
        }
    }
}