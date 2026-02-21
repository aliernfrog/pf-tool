package io.github.aliernfrog.pftool_shared.enum

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import kotlin.reflect.KProperty1

enum class ListStyle(
    val label: KProperty1<PFToolSharedString, Int>,
    val iconVector: ImageVector
) {
    LIST(
        label = PFToolSharedString::listStyleList,
        iconVector = Icons.Default.ViewStream
    ),
    GRID(
        label = PFToolSharedString::listStyleGrid,
        iconVector = Icons.Default.GridView
    )
}