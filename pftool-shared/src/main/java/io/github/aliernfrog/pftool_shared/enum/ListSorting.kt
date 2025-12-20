package io.github.aliernfrog.pftool_shared.enum

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.shared.util.SharedString

enum class ListSorting(
    val label: SharedString,
    val iconVector: ImageVector
) {
    ALPHABETICAL(
        label = PFToolSharedString.ListSortingName,
        iconVector = Icons.Default.SortByAlpha
    ),

    DATE(
        label = PFToolSharedString.ListSortingDate,
        iconVector = Icons.Default.CalendarMonth
    ),

    SIZE(
        label = PFToolSharedString.ListSortingSize,
        iconVector = Icons.AutoMirrored.Filled.Note
    )
}