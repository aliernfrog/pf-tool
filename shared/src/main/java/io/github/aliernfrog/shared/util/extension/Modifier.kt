package io.github.aliernfrog.shared.util.extension

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

fun Modifier.clickableWithColor(
    color: Color,
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit
): Modifier {
    return this.clickable(
        interactionSource = interactionSource,
        indication = ripple(color = color),
        onClick = onClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.combinedClickableWithColor(
    color: Color,
    onLongClick: () -> Unit,
    onClick: () -> Unit
): Modifier {
    return this.combinedClickable(
        interactionSource = null,
        indication = ripple(color = color),
        onLongClick = onLongClick,
        onClick = onClick
    )
}


fun Modifier.horizontalFadingEdge(
    scrollState: ScrollState
): Modifier {
    return this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            val isRTL = layoutDirection == LayoutDirection.Rtl
            val lengthPx = 100.dp.toPx()

            val leftHidden = if (isRTL) scrollState.maxValue - scrollState.value else scrollState.value
            val rightHidden = if (isRTL) scrollState.value else scrollState.maxValue - scrollState.value

            val leftStrength = (leftHidden / lengthPx).coerceAtMost(1f)
            val rightStrength = (rightHidden / lengthPx).coerceAtMost(1f)

            drawContent()

            drawRect(
                brush = Brush.horizontalGradient(
                    0f to Color.Transparent,
                    (leftStrength * lengthPx / size.width).coerceIn(0f, 1f) to Color.Black,
                    (1f - (rightStrength * lengthPx / size.width)).coerceIn(0f, 1f) to Color.Black,
                    1f to Color.Transparent
                ),
                blendMode = BlendMode.DstIn
            )
        }
}