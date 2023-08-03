package com.aliernfrog.pftool

import android.os.Environment
import com.aliernfrog.pftool.data.PrefEditItem
import com.aliernfrog.pftool.data.Social

const val experimentalSettingsRequiredClicks = 10
const val githubRepoURL = "https://github.com/aliernfrog/pf-tool"

object ConfigKey {
    const val PREF_NAME = "APP_CONFIG"
    const val KEY_APP_THEME = "appTheme"
    const val KEY_APP_MATERIAL_YOU = "materialYou"
    const val KEY_APP_AUTO_UPDATES = "autoUpdates"
    const val KEY_APP_UPDATES_URL = "updatesUrl"
    const val KEY_SHOW_MAP_THUMBNAILS_LIST = "showMapThumbnailsList"
    const val KEY_MAPS_DIR = "mapsDir"
    const val KEY_MAPS_EXPORT_DIR = "mapsExportDir"
    const val DEFAULT_UPDATES_URL = "https://aliernfrog.github.io/pftool/latest.json"
    val DEFAULT_MAPS_DIR = "${Environment.getExternalStorageDirectory()}/Android/data/com.MA.Polyfield/files/editor"
    val DEFAULT_MAPS_EXPORT_DIR = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/PFTool/exported"
}

object SettingsConstant {
    val socials = listOf(
        Social("Polyfield Discord", "https://discord.gg/X6WzGpCgDJ"),
        Social("PF Tool GitHub", githubRepoURL)
    )
    val experimentalPrefOptions = listOf(
        PrefEditItem(ConfigKey.KEY_APP_UPDATES_URL, ConfigKey.DEFAULT_UPDATES_URL),
        PrefEditItem(ConfigKey.KEY_MAPS_DIR, ConfigKey.DEFAULT_MAPS_DIR),
        PrefEditItem(ConfigKey.KEY_MAPS_EXPORT_DIR, ConfigKey.DEFAULT_MAPS_EXPORT_DIR)
    )
}