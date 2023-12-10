package com.aliernfrog.pftool

import android.os.Build
import android.os.Environment
import com.aliernfrog.pftool.data.PrefEditItem
import com.aliernfrog.pftool.data.Social

const val TAG = "PFToolLogs"

const val experimentalSettingsRequiredClicks = 10
const val githubRepoURL = "https://github.com/aliernfrog/pf-tool"

val externalStorageRoot = Environment.getExternalStorageDirectory().toString()+"/"
val folderPickerSupportsInitialUri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

/**
 * False if the device has Android October 2023 security patches, which made requesting access to Android/data impossible.
 */
val canRequestAndroidDataAccess: Boolean = false /* TODO try {
    val securityPatchSplit = Build.VERSION.SECURITY_PATCH.split("-") // 2023-10-01 (YYYY-MM-DD)
    val year = securityPatchSplit[0].toInt()
    val month = securityPatchSplit[1].toInt()
    if (year == 2023) month < 10
    else year < 2023
} catch (_: Exception) {
    true
}*/

object ConfigKey {
    const val PREF_NAME = "APP_CONFIG"
    const val KEY_APP_THEME = "appTheme"
    const val KEY_APP_MATERIAL_YOU = "materialYou"
    const val KEY_APP_AUTO_UPDATES = "autoUpdates"
    const val KEY_APP_UPDATES_URL = "updatesUrl"
    const val KEY_SHOW_CHOSEN_MAP_THUMBNAIL = "showChosenMapThumbnail"
    const val KEY_SHOW_MAP_THUMBNAILS_LIST = "showMapThumbnailsList"
    const val KEY_MAPS_DIR = "mapsDir"
    const val KEY_EXPORTED_MAPS_DIR = "mapsExportDir"
    const val DEFAULT_UPDATES_URL = "https://aliernfrog.github.io/pftool/latest.json"
    val RECOMMENDED_MAPS_DIR = "${Environment.getExternalStorageDirectory()}/Android/data/com.MA.Polyfield/files/editor"
    val RECOMMENDED_EXPORTED_MAPS_DIR = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/PFTool/exported"
}

object SettingsConstant {
    val socials = listOf(
        Social("Polyfield Discord", "https://discord.gg/X6WzGpCgDJ"),
        Social("PF Tool GitHub", githubRepoURL)
    )
    val folders = listOf(
        PrefEditItem(
            labelResourceId = R.string.settings_general_folders_maps,
            getValue = { it.pfMapsDir },
            setValue = { newValue, prefs ->
                prefs.pfMapsDir = newValue
            },
            default = ConfigKey.RECOMMENDED_MAPS_DIR
        ),
        PrefEditItem(
            labelResourceId = R.string.settings_general_folders_exportedMaps,
            getValue = { it.exportedMapsDir },
            setValue = { newValue, prefs ->
                prefs.exportedMapsDir = newValue
            },
            default = ConfigKey.RECOMMENDED_EXPORTED_MAPS_DIR
        )
    )
    val experimentalPrefOptions = listOf(
        PrefEditItem(
            labelResourceId = R.string.settings_experimental_updatesURL,
            getValue = { it.updatesURL },
            setValue = { newValue, prefs ->
                prefs.updatesURL = newValue
            },
            default = ConfigKey.DEFAULT_UPDATES_URL
        ),
        *folders.toTypedArray()
    )
}