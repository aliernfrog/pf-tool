package com.aliernfrog.pftool.ui.sheet

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.enum.ListSorting
import com.aliernfrog.pftool.enum.ListStyle
import com.aliernfrog.pftool.ui.component.AppModalBottomSheet
import com.aliernfrog.pftool.ui.component.FadeVisibility
import com.aliernfrog.pftool.ui.component.expressive.ExpressiveSection
import com.aliernfrog.pftool.util.extension.horizontalFadingEdge
import com.aliernfrog.pftool.util.manager.base.BasePreferenceManager

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
        if (isAnySortingOptionAvailable) ExpressiveSection(stringResource(R.string.list_sorting)) {
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
                                Text(stringResource(entry.label))
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
                            Text(stringResource(R.string.list_sorting_reversed))
                        },
                        leadingIcon = {
                            Icon(Icons.Default.SwapVert, null)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }

        ExpressiveSection(stringResource(R.string.list_style)) {
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
                            Text(stringResource(entry.label))
                        },
                        leadingIcon = {
                            Icon(entry.iconVector, null)
                        }
                    )
                }
            }

            FadeVisibility(style == ListStyle.GRID) {
                ExpressiveSection(
                    title = stringResource(R.string.list_style_gridMaxLineSpan)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = gridMaxLineSpan.toFloat(),
                            onValueChange = { onGridMaxLineSpanChange(it.toInt()) },
                            valueRange = 1f..10f,
                            steps = 8,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListViewOptionsSheet(
    sheetState: SheetState,
    listViewOptionsPreference: BasePreferenceManager.ListViewOptionsPreference
) {
    val listStylePref = listViewOptionsPreference.styleGroup.getCurrent()
    val gridMaxLineSpanPref = listViewOptionsPreference.gridMaxLineSpanGroup.getCurrent()

    ListViewOptionsSheet(
        sheetState = sheetState,
        sorting = listViewOptionsPreference.sorting.let {
            ListSorting.entries[it.value]
        },
        onSortingChange = listViewOptionsPreference.sorting.let { pref -> {
            pref.value = it.ordinal
        } },
        sortingReversed = listViewOptionsPreference.sortingReversed.value,
        onSortingReversedChange = listViewOptionsPreference.sortingReversed.let { pref ->{
            pref.value = it
        } },
        style = ListStyle.entries[listStylePref.value],
        onStyleChange = { listStylePref.value = it.ordinal },
        gridMaxLineSpan = gridMaxLineSpanPref.value,
        onGridMaxLineSpanChange = { gridMaxLineSpanPref.value = it }
    )
}