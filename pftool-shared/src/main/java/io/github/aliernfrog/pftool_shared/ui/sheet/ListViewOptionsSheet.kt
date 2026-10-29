package io.github.aliernfrog.pftool_shared.ui.sheet

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.ViewColumn
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.pftool_shared.enum.ListSorting
import io.github.aliernfrog.pftool_shared.enum.ListStyle
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.pftool_shared.util.manager.base.PFToolBasePreferenceManager
import io.github.aliernfrog.pftool_shared.util.sharedStringResource
import io.github.aliernfrog.shared.ui.component.AppModalBottomSheet
import io.github.aliernfrog.shared.ui.component.FadeVisibility
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection

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
    onGridMaxLineSpanChange: (Int) -> Unit,
    extraStyleOptions: @Composable () -> Unit = {}
) {
    val isAnySortingOptionAvailable = onSortingChange != null || onSortingReversedChange != null
    AppModalBottomSheet(
        sheetState = sheetState
    ) {
        if (isAnySortingOptionAvailable) ExpressiveSection(
            title = sharedStringResource(PFToolSharedString::listSorting),
            leadingIcon = rememberVectorPainter(Icons.AutoMirrored.Rounded.Sort)
        ) {
            onSortingChange?.let { sortingChangeCallback ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    val sortingEntries = remember { ListSorting.entries }
                    sortingEntries.forEachIndexed { index, entry ->
                        val selected = entry == sorting
                        val weight by animateFloatAsState(if (selected) 1.1f else 1f)
                        ToggleButton(
                            checked = entry == sorting,
                            onCheckedChange = {
                                if (it) sortingChangeCallback(entry)
                            },
                            shapes = when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                sortingEntries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            },
                            modifier = Modifier
                                .animateContentSize()
                                .weight(weight)
                        ) {
                            Icon(
                                imageVector = entry.iconVector,
                                contentDescription = null,
                                modifier = Modifier.size(ToggleButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.width(ToggleButtonDefaults.IconSpacing))
                            Text(sharedStringResource(entry.label))
                        }
                    }
                }

                onSortingReversedChange?.let { sortingReversedChangeCallback ->
                    ToggleButton(
                        checked = sortingReversed == true,
                        onCheckedChange = { sortingReversedChangeCallback(it) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = null,
                            modifier = Modifier.size(ToggleButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.width(ToggleButtonDefaults.IconSpacing))
                        Text(sharedStringResource(PFToolSharedString::listSortingReversed))
                    }
                }
            }
        }

        ExpressiveSection(
            title = sharedStringResource(PFToolSharedString::listStyle),
            leadingIcon = rememberVectorPainter(Icons.Rounded.Style)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                val styleEntries = remember { ListStyle.entries.reversed() }
                styleEntries.forEachIndexed { index, entry ->
                    val selected = entry == style
                    val weight by animateFloatAsState(if (selected) 1.1f else 1f)
                    ToggleButton(
                        checked = entry == style,
                        onCheckedChange = {
                            if (it) onStyleChange(entry)
                        },
                        shapes = when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            styleEntries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                        modifier = Modifier
                            .animateContentSize()
                            .weight(weight)
                    ) {
                        Icon(
                            imageVector = entry.iconVector,
                            contentDescription = null,
                            modifier = Modifier.size(ToggleButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.width(ToggleButtonDefaults.IconSpacing))
                        Text(sharedStringResource(entry.label))
                    }
                }
            }

            extraStyleOptions()

            FadeVisibility(style == ListStyle.GRID) {
                ExpressiveSection(
                    title = sharedStringResource(PFToolSharedString::listStyleGridMaxLineSpan),
                    leadingIcon = rememberVectorPainter(Icons.Rounded.ViewColumn)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val sliderState = rememberSliderState(
                            value = gridMaxLineSpan.toFloat(),
                            steps = 4,
                            trackRange = 1f..6f
                        )
                        Slider(
                            state = sliderState,
                            onValueChange = {
                                onGridMaxLineSpanChange(it.toInt())
                                sliderState.value = it
                            },
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
    listViewOptionsPreference: PFToolBasePreferenceManager.ListViewOptionsPreference,
    extraStyleOptions: @Composable () -> Unit = {}
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
        onGridMaxLineSpanChange = { gridMaxLineSpanPref.value = it },
        extraStyleOptions = extraStyleOptions
    )
}