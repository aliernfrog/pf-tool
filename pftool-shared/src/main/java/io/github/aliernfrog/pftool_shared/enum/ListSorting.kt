package io.github.aliernfrog.pftool_shared.enum

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.aliernfrog.pftool_shared.util.SharedString

enum class ListSorting(
    val label: SharedString,
    val iconVector: ImageVector
) {
    ALPHABETICAL(
        label = SharedString.LIST_SORTING_NAME,
        iconVector = Icons.Default.SortByAlpha
    ),

    DATE(
        label = SharedString.LIST_SORTING_DATE,
        iconVector = Icons.Default.CalendarMonth
    ),

    SIZE(
        label = SharedString.LIST_SORTING_SIZE,
        iconVector = Icons.AutoMirrored.Filled.Note
    )
}