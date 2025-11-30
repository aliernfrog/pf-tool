package io.github.aliernfrog.pftool_shared.enum

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.aliernfrog.pftool_shared.util.SharedString

enum class ListStyle(
    val label: SharedString,
    val iconVector: ImageVector
) {
    LIST(
        label = SharedString.LIST_STYLE_LIST,
        iconVector = Icons.Default.ViewStream
    ),
    GRID(
        label = SharedString.LIST_STYLE_GRID,
        iconVector = Icons.Default.GridView
    )
}