package io.github.aliernfrog.pftool_shared.ui.sheet

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.pftool_shared.enum.ListSorting
import io.github.aliernfrog.pftool_shared.enum.ListStyle
import io.github.aliernfrog.pftool_shared.ui.component.AppModalBottomSheet
import io.github.aliernfrog.pftool_shared.ui.component.FadeVisibility
import io.github.aliernfrog.pftool_shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.pftool_shared.util.SharedString
import io.github.aliernfrog.pftool_shared.util.extension.horizontalFadingEdge
import io.github.aliernfrog.pftool_shared.util.sharedStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListViewOptionsSheet(
    sheetState: SheetState,
    sorting: ListSorting?,
    onSortingChange: ((ListSorting) -> Unit)?,
    sortingReversed: Boolean?,
    onSortingReversedChange: ((Boolean) -> Unit)?,
    style: ListStyle,
    onStyleChange: (ListStyle) -> Unit,
    gridMaxLineSpan: Int,
    onGridMaxLineSpanChange: (Int) -> Unit
) {
    val isAnySortingOptionAvailable = onSortingChange != null || onSortingReversedChange != null
    AppModalBottomSheet(
        sheetState = sheetState
    ) {
        if (isAnySortingOptionAvailable) ExpressiveSection(
            title = sharedStringResource(SharedString.LIST_SORTING)
        ) {
            onSortingChange?.let { sortingChangeCallback ->
                val filtersScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .horizontalFadingEdge(
                            scrollState = filtersScrollState,
                            edgeColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            isRTL = LocalLayoutDirection.current == LayoutDirection.Rtl
                        )
                        .horizontalScroll(filtersScrollState)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ListSorting.entries.forEach { entry ->
                        FilterChip(
                            selected = entry == sorting,
                            onClick = { sortingChangeCallback(entry) },
                            label = {
                                Text(sharedStringResource(entry.label))
                            },
                            leadingIcon = {
                                Icon(entry.iconVector, null)
                            }
                        )
                    }
                }

                onSortingReversedChange?.let { sortingReversedChangeCallback ->
                    InputChip(
                        selected = sortingReversed == true,
                        onClick = { sortingReversedChangeCallback(sortingReversed != true) },
                        label = {
                            Text(sharedStringResource(SharedString.LIST_SORTING_REVERSED))
                        },
                        leadingIcon = {
                            Icon(Icons.Default.SwapVert, null)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }

        ExpressiveSection(sharedStringResource(SharedString.LIST_STYLE)) {
            val stylesScrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .horizontalFadingEdge(
                        scrollState = stylesScrollState,
                        edgeColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        isRTL = LocalLayoutDirection.current == LayoutDirection.Rtl
                    )
                    .horizontalScroll(stylesScrollState)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ListStyle.entries.forEach { entry ->
                    FilterChip(
                        selected = entry == style,
                        onClick = { onStyleChange(entry) },
                        label = {
                            Text(sharedStringResource(entry.label))
                        },
                        leadingIcon = {
                            Icon(entry.iconVector, null)
                        }
                    )
                }
            }

            FadeVisibility(style == ListStyle.GRID) {
                ExpressiveSection(
                    title = sharedStringResource(SharedString.LIST_STYLE_GRID_MAX_LINE_SPAN)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = gridMaxLineSpan.toFloat(),
                            onValueChange = { onGridMaxLineSpanChange(it.toInt()) },
                            valueRange = 1f..6f,
                            steps = 4,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp, end = 4.dp)
                        )
                        Text(
                            text = gridMaxLineSpan.toString(),
                            modifier = Modifier
                                .animateContentSize()
                                .padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }
    }
}