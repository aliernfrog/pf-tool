package com.aliernfrog.pftool

import android.os.Build
import android.os.Environment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import com.aliernfrog.pftool.data.PrefEditItem
import com.aliernfrog.pftool.data.Social
import com.aliernfrog.pftool.util.staticutil.GeneralUtil

const val TAG = "PFToolLogs"

const val experimentalSettingsRequiredClicks = 10
const val githubRepoURL = "https://github.com/aliernfrog/pf-tool"
const val crowdinURL = "https://crowdin.com/project/pf-tool"

val externalStorageRoot = Environment.getExternalStorageDirectory().toString()+"/"
val supportsPerAppLanguagePreferences = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
val folderPickerSupportsInitialUri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
val hasAndroidDataRestrictions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

object ConfigKey {
    const val PREF_NAME = "APP_CONFIG"
    const val KEY_APP_LANGUAGE = "appLanguage"
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
        Social(
            label = "Polyfield",
            icon = R.drawable.discord,
            url = "https://discord.gg/X6WzGpCgDJ"
        ),
        Social(
            label = "PF Tool",
            icon = R.drawable.github,
            url = githubRepoURL
        ),
        Social(
            label = "Crowdin",
            icon = Icons.Default.Translate,
            url = crowdinURL
        )
    )
    val folders = listOf(
        PrefEditItem(
            labelResourceId = R.string.settings_storage_folders_maps,
            getValue = { it.pfMapsDir },
            setValue = { newValue, prefs ->
                prefs.pfMapsDir = newValue
            },
            default = ConfigKey.RECOMMENDED_MAPS_DIR
        ),
        PrefEditItem(
            labelResourceId = R.string.settings_storage_folders_exportedMaps,
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

val languages = BuildConfig.LANGUAGES.sorted().map { langCode ->
    GeneralUtil.getLanguageFromCode(langCode)!!
}