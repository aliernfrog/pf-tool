package io.github.aliernfrog.shared.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FloatingActionButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    text: String? = null,
    expanded: Boolean = true,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    onClick: () -> Unit
) {
    if (text == null) FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor
        )
    }
    else SmallExtendedFloatingActionButton(
        text = {
            Text(text)
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = contentColor
            )
        },
        onClick = onClick,
        modifier = modifier,
        expanded = expanded,
        shape = FloatingActionButtonDefaults.smallExtendedFabShape,
        containerColor = containerColor,
        contentColor = contentColor
    )
}

@Composable
fun ToggleFloatingActionButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (iconSize: Dp) -> Unit
) {
    val containerColor by animateColorAsState(
        if (checked) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.primaryContainer
    )
    val contentColor by animateColorAsState(
        if (checked) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onPrimaryContainer
    )
    val roundness by animateDpAsState(
        if (checked) 200.dp else 16.dp
    )

    FloatingActionButton(
        onClick = { onCheckedChange(!checked) },
        shape = RoundedCornerShape(roundness),
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier
    ) {
        icon(24.dp)
    }
}