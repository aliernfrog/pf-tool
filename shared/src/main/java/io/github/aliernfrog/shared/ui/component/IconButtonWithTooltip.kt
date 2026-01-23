package io.github.aliernfrog.shared.ui.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IconButtonWithTooltip(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlainTextTooltipContainer(
        tooltipText = contentDescription,
        modifier = modifier
    ) {
        IconButton(
            onClick = onClick,
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                painter = icon, contentDescription = contentDescription
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FilledIconButtonWithTooltip(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlainTextTooltipContainer(
        tooltipText = contentDescription,
        modifier = modifier
    ) {
        FilledIconButton(
            onClick = onClick,
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                painter = icon, contentDescription = contentDescription
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FilledTonalIconButtonWithTooltip(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlainTextTooltipContainer(
        tooltipText = contentDescription,
        modifier = modifier
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                painter = icon, contentDescription = contentDescription
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlainTextTooltipContainer(
    tooltipText: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val tooltipState = rememberTooltipState()
    TooltipBox(
        tooltip = {
            PlainTooltip {
                Text(tooltipText)
            }
        },
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        state = tooltipState,
        modifier = modifier,
        content = content
    )
}