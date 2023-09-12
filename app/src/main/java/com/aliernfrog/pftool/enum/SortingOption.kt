package com.aliernfrog.pftool.enum

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.ui.graphics.vector.ImageVector
import com.aliernfrog.pftool.R

enum class SortingOption(
    val labelId: Int,
    val iconVector: ImageVector
) {
    ALPHABETICAL(
        labelId = R.string.mapsList_sorting_name,
        iconVector = Icons.Default.SortByAlpha
    ),

    DATE(
        labelId = R.string.mapsList_sorting_date,
        iconVector = Icons.Default.CalendarMonth
    ),

    SIZE(
        labelId = R.string.mapsList_sorting_size,
        iconVector = Icons.Default.Note
    )
}