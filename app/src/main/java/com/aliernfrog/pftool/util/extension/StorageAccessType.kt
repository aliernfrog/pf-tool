package com.aliernfrog.pftool.util.extension

import com.aliernfrog.pftool.util.manager.PreferenceManager
import io.github.aliernfrog.pftool_shared.enum.StorageAccessType
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.di.getKoinInstance

fun StorageAccessType.enable() {
    val prefs = getKoinInstance<PreferenceManager>()
    prefs.storageAccessType.value = this.ordinal

    when (this) {
        StorageAccessType.SAF -> {
            prefs.pfMapsDir.value = PFToolSharedUtil.getTreeUriForPath(prefs.pfMapsDir.value).toString()
            prefs.exportedMapsDir.value = PFToolSharedUtil.getTreeUriForPath(prefs.exportedMapsDir.value).toString()
        }
        StorageAccessType.SHIZUKU, StorageAccessType.ALL_FILES -> {
            prefs.pfMapsDir.value = PFToolSharedUtil.getFilePath(prefs.pfMapsDir.value)
            prefs.exportedMapsDir.value = PFToolSharedUtil.getFilePath(prefs.exportedMapsDir.value)
        }
    }
}