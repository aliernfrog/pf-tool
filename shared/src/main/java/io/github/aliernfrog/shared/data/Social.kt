package io.github.aliernfrog.shared.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource

data class Social(
    val label: Any,
    val icon: Any,
    val iconContainerColor: Color = Color.Blue,
    val url: String
)

@Composable
fun Social.getIconPainter(): Painter {
    return when (icon) {
        is Int -> painterResource(icon)
        is ImageVector -> rememberVectorPainter(icon)
        else -> throw IllegalArgumentException("unexpected class for social icon")
    }
}