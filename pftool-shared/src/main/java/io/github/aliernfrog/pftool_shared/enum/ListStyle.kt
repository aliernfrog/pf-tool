package io.github.aliernfrog.pftool_shared.enum

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.shared.util.SharedString

enum class ListStyle(
    val label: SharedString,
    val iconVector: ImageVector
) {
    LIST(
        label = PFToolSharedString.ListStyleList,
        iconVector = Icons.Default.ViewStream
    ),
    GRID(
        label = PFToolSharedString.ListStyleGrid,
        iconVector = Icons.Default.GridView
    )
}