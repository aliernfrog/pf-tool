package com.aliernfrog.pftool

import android.os.Environment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.data.Social

val AppRoundnessSize = 28.dp
val AppComponentShape = RoundedCornerShape(AppRoundnessSize)
const val experimentalSettingsRequiredClicks = 10

object ConfigKey {
    const val PREF_NAME = "APP_CONFIG"
    const val KEY_APP_THEME = "appTheme"
    const val KEY_APP_MATERIAL_YOU = "materialYou"
    const val KEY_SHOW_MAP_THUMBNAILS_LIST = "showMapThumbnailsList"
    const val KEY_MAPS_DIR = "mapsDir"
    const val KEY_MAPS_EXPORT_DIR = "mapsExportDir"
    val DEFAULT_MAPS_DIR = "${Environment.getExternalStorageDirectory()}/Android/data/com.MA.Polyfield/files/editor"
    val DEFAULT_MAPS_EXPORT_DIR = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/PFTool/exported"
}

object Link {
    val socials = listOf(
        Social("Polyfield Discord", "https://discord.gg/X6WzGpCgDJ"),
        Social("PF Tool GitHub", "https://github.com/aliernfrog/pf-tool")
    )
}