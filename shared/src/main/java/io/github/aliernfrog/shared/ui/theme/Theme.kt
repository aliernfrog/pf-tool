package io.github.aliernfrog.shared.ui.theme

import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.aliernfrog.shared.util.SharedString
import kotlin.reflect.KProperty1

val supportsMaterialYou = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

enum class Theme(
    val label: KProperty1<SharedString, Int>,
    val outlinedIcon: ImageVector,
    val filledIcon: ImageVector
) {
    SYSTEM(
        label = SharedString::settingsAppearanceThemeSystem,
        outlinedIcon = Icons.Outlined.BrightnessAuto,
        filledIcon = Icons.Default.BrightnessAuto
    ),

    LIGHT(
        label = SharedString::settingsAppearanceThemeLight,
        outlinedIcon = Icons.Outlined.LightMode,
        filledIcon = Icons.Default.LightMode
    ),

    DARK(
        label = SharedString::settingsAppearanceThemeDark,
        outlinedIcon = Icons.Outlined.DarkMode,
        filledIcon = Icons.Default.DarkMode
    )
}