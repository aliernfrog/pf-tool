package com.aliernfrog.pftool.enum

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.ui.graphics.vector.ImageVector
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.impl.FileWrapper

enum class ListSorting(
    @StringRes val label: Int,
    val iconVector: ImageVector,
    val comparator: Comparator<FileWrapper>
) {
    ALPHABETICAL(
        label = R.string.list_sorting_name,
        iconVector = Icons.Default.SortByAlpha,
        comparator = compareBy { it.name.lowercase() }
    ),

    DATE(
        label = R.string.list_sorting_date,
        iconVector = Icons.Default.CalendarMonth,
        comparator = compareByDescending(FileWrapper::lastModified)
    ),

    SIZE(
        label = R.string.list_sorting_size,
        iconVector = Icons.AutoMirrored.Filled.Note,
        comparator = compareByDescending(FileWrapper::size)
    )
}