package com.aliernfrog.pftool.ui.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.activity.MainActivity

val supportsMaterialYou = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("NewApi")
@Composable
fun PFToolTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColors: Boolean = true,
    pitchBlack: Boolean = false,
    content: @Composable () -> Unit
) {
    val useDynamicColors = supportsMaterialYou && dynamicColors
    val colors = when {
        useDynamicColors && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        useDynamicColors && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> darkColorScheme()
        else -> expressiveLightColorScheme()
    }.let {
        if (darkTheme && pitchBlack) it.copy(background = Color.Black, surface = Color.Black)
        else it
    }

    val view = LocalView.current
    if (!view.isInEditMode) SideEffect {
        if (view.context !is MainActivity) return@SideEffect
        val activity = view.context as Activity
        val insetsController = WindowCompat.getInsetsController(activity.window, view)

        WindowCompat.setDecorFitsSystemWindows(activity.window, false)

        if (Build.VERSION.SDK_INT >= 23) Color.Transparent.toArgb().let {
            @Suppress("DEPRECATION")
            activity.window.statusBarColor = it
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= 24) activity.window.navigationBarColor = it
        }

        if (Build.VERSION.SDK_INT >= 29) {
            activity.window.isNavigationBarContrastEnforced = false
        }

        insetsController.isAppearanceLightStatusBars = !darkTheme
        insetsController.isAppearanceLightNavigationBars = !darkTheme
    }

    MaterialExpressiveTheme(
        colorScheme = colors,
        motionScheme = MotionScheme.expressive(),
        shapes = Shapes,
        typography = Typography,
        content = content
    )
}

enum class Theme(
    @StringRes val label: Int
) {
    SYSTEM(R.string.settings_appearance_theme_system),
    LIGHT(R.string.settings_appearance_theme_light),
    DARK(R.string.settings_appearance_theme_dark)
}