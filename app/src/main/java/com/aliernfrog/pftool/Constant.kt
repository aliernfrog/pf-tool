package com.aliernfrog.pftool

import android.os.Environment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.data.Screen
import com.aliernfrog.pftool.data.Social

val PFToolRoundnessSize = 30.dp
val PFToolComposableShape = RoundedCornerShape(PFToolRoundnessSize)

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

object NavRoutes {
    const val MAPS = "maps"
    const val OPTIONS = "options"
}

@Composable
fun getScreens(): List<Screen> {
    return listOf(
        Screen(NavRoutes.MAPS, stringResource(R.string.manageMaps), rememberVectorPainter(Icons.Default.PinDrop), rememberVectorPainter(Icons.Outlined.PinDrop)),
        Screen(NavRoutes.OPTIONS, stringResource(R.string.options), rememberVectorPainter(Icons.Default.Settings), rememberVectorPainter(Icons.Outlined.Settings))
    )
}

object PickMapSheetSegments {
    const val IMPORTED = 0
    const val EXPORTED = 1
}

object Theme {
    const val SYSTEM = 0
    const val LIGHT = 1
    const val DARK = 2
}