package io.github.aliernfrog.pftool_shared.enum

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.aliernfrog.pftool_shared.impl.FileWrapper
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import kotlin.reflect.KProperty1

enum class ListSorting(
    val label: KProperty1<PFToolSharedString, Int>,
    val iconVector: ImageVector,
    val comparator: Comparator<FileWrapper>
) {
    ALPHABETICAL(
        label = PFToolSharedString::listSortingName,
        iconVector = Icons.Default.SortByAlpha,
        comparator = compareBy(FileWrapper::name)
    ),

    DATE(
        label = PFToolSharedString::listSortingDate,
        iconVector = Icons.Default.CalendarMonth,
        comparator = compareByDescending(FileWrapper::lastModified)
    ),

    SIZE(
        label = PFToolSharedString::listSortingSize,
        iconVector = Icons.AutoMirrored.Filled.Note,
        comparator = compareBy(FileWrapper::size)
    )
}