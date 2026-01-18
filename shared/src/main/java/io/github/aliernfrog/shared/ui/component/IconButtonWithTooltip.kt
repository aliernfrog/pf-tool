package io.github.aliernfrog.shared.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IconButtonWithTooltip(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tooltipState = rememberTooltipState()
    TooltipBox(
        tooltip = {
            PlainTooltip {
                Text(contentDescription)
            }
        },
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        state = tooltipState,
        modifier = modifier
    ) {
        IconButton(
            onClick = onClick,
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                painter = painter, contentDescription = contentDescription
            )
        }
    }
}