package io.github.aliernfrog.shared.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ErrorWithIcon(
    icon: Painter,
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    visible: Boolean = true,
    textOpacity: Float = 1f,
    iconContainerShape: Shape = MaterialShapes.Ghostish.toShape(),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    iconContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    button: (@Composable () -> Unit)? = null
) {
    FadeVisibility(visible) {
        Column(
            modifier = modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = contentColorFor(iconContainerColor),
                modifier = Modifier
                    .size(80.dp)
                    .clip(iconContainerShape)
                    .background(iconContainerColor)
                    .padding(16.dp)
            )

            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    textAlign = TextAlign.Center,
                    color = contentColor,
                    modifier = Modifier.alpha(textOpacity)
                )
            }

            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = contentColor,
                    modifier = Modifier.alpha(textOpacity)
                )
            }

            button?.invoke()
        }
    }
}