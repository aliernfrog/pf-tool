package io.github.aliernfrog.shared.ui.component

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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