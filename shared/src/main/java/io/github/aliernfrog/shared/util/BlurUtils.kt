package io.github.aliernfrog.shared.util

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.blur.material3.Material3

val LocalBlurEnabledValue = compositionLocalOf { true }

@Composable
fun Modifier.toggledHazeBlur(
    containerColor: Color,
    containerOpacity: Float,
    input: HazeInput,
    style: HazeBlurStyle = HazeBlurStyle.Material3(
        containerColor = containerColor
    )
): Modifier {
    val blurEnabled = LocalBlurEnabledValue.current
    return this
        .let {
            if (blurEnabled) it.hazeBlur(
                input = input,
                style = style
            ) else it
        }
        .background(
            containerColor.copy(
                alpha = if (blurEnabled) containerOpacity else 1f
            )
        )
}