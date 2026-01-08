package com.aliernfrog.pftool.util.manager

import android.content.Context
import android.os.Environment
import io.github.aliernfrog.pftool_shared.enum.StorageAccessType
import io.github.aliernfrog.pftool_shared.util.externalStorageRoot
import io.github.aliernfrog.pftool_shared.util.manager.base.PFToolBasePreferenceManager
import io.github.aliernfrog.shared.ui.theme.Theme
import io.github.aliernfrog.shared.util.extension.getAppVersionCode

class PreferenceManager(context: Context) : PFToolBasePreferenceManager(
    prefs = context.getSharedPreferences("APP_CONFIG", Context.MODE_PRIVATE)
) {
    // Appearance options
    val theme = intPreference("appTheme", Theme.SYSTEM.ordinal)
    val materialYou = booleanPreference("materialYou", true)
    val pitchBlack = booleanPreference("pitchBlack", false)

    // General options
    val language = stringPreference("appLanguage", "") // follow system if blank
    val autoCheckUpdates = booleanPreference("autoUpdates", true)

    // Maps options
    val showChosenMapThumbnail = booleanPreference("showChosenMapThumbnail", true)
    val showMapThumbnailsInList = booleanPreference("showMapThumbnailsList", true)
    val stackupMaps = booleanPreference("stackupMaps", false)

    // Storage options
    val pfMapsDir = stringPreference("mapsDir", "${externalStorageRoot}Android/data/com.MA.Polyfield/files/editor", experimental = true)
    val exportedMapsDir = stringPreference("mapsExportDir", "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/PFTool/exported", experimental = true)
    val storageAccessType = intPreference("storageAccessType", StorageAccessType.SAF.ordinal, includeInDebugInfo = true)

    // Maps list
    val mapsListViewOptions = listViewOptionsPreference("mapsList", defaultGridMaxLineSpans = WindowSizeClassValueGroup(
        compact = 2, medium = 4, expanded = 5
    ))

    // Other options
    val showMapNameFieldGuide = booleanPreference("showMapNameFieldGuide", true, experimental = true, includeInDebugInfo = false)
    val showMediaViewGuide = booleanPreference("showMediaViewGuide", true, experimental = true, includeInDebugInfo = false)

    // Experimental (developer) options
    val experimentalOptionsEnabled = booleanPreference("experimentalOptionsEnabled", false)
    val ignoreDocumentsUIRestrictions = booleanPreference("ignoreDocumentsUiRestrictions", false, experimental = true, includeInDebugInfo = false)
    val shizukuNeverLoad = booleanPreference("shizukuNeverLoad", false, experimental = true, includeInDebugInfo = false)
    val lastKnownInstalledVersion = longPreference("lastKnownInstalledVersion", context.getAppVersionCode(), experimental = true, includeInDebugInfo = false)
    val updatesURL = stringPreference("updatesUrl", "https://aliernfrog.github.io/pftool/latest.json", experimental = true, includeInDebugInfo = false)
}