package io.github.aliernfrog.pftool_shared.ui.theme

import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.aliernfrog.pftool_shared.util.SharedString

val supportsMaterialYou = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

enum class Theme(
    val label: SharedString,
    val outlinedIcon: ImageVector,
    val filledIcon: ImageVector
) {
    SYSTEM(
        label = SharedString.SETTINGS_APPEARANCE_THEME_SYSTEM,
        outlinedIcon = Icons.Outlined.BrightnessAuto,
        filledIcon = Icons.Default.BrightnessAuto
    ),

    LIGHT(
        label = SharedString.SETTINGS_APPEARANCE_THEME_LIGHT,
        outlinedIcon = Icons.Outlined.LightMode,
        filledIcon = Icons.Default.LightMode
    ),

    DARK(
        label = SharedString.SETTINGS_APPEARANCE_THEME_DARK,
        outlinedIcon = Icons.Outlined.DarkMode,
        filledIcon = Icons.Default.DarkMode
    )
}