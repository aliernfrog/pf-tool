package com.aliernfrog.pftool.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    background = BackgroundLight,
    surface = BackgroundLight,
    primary = Blue,
    primaryVariant = Blue,
    secondary = LinearLight,
    secondaryVariant = ButtonLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val DarkColorPalette = darkColors(
    background = BackgroundDark,
    surface = BackgroundDark,
    primary = Blue,
    primaryVariant = Blue,
    secondary = LinearDark,
    secondaryVariant = ButtonDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

val Colors.sheetScrim: Color
get() = if (isLight) SheetScrimLight else SheetScrimDark

val Colors.sheetHandleBar: Color
get() = if (isLight) ButtonLight else ButtonLight

@Composable
fun PFToolTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}