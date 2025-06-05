package com.aliernfrog.pftool.ui.viewmodel

import android.content.Context
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.pftool.enum.MapAction
import com.aliernfrog.pftool.enum.MapsListSegment
import com.aliernfrog.pftool.enum.ListSorting
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.ui.component.createSheetStateWithDensity
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MapsListViewModel(
    val topToastState: TopToastState,
    val prefs: PreferenceManager,
    private val mapsViewModel: MapsViewModel,
    context: Context
) : ViewModel() {
    var searchQuery by mutableStateOf("")
    val selectedMaps = mutableStateListOf<MapFile>()
    val availableSegments = mutableStateListOf<MapsListSegment>()
    val pagerState = PagerState { availableSegments.size }
    val listViewOptionsSheetState = createSheetStateWithDensity(skipPartiallyExpanded = true, Density(context))

    val selectedMapsActions: List<MapAction>
        get() = MapAction.entries.filter { action ->
            action.availableForMultiSelection && !selectedMaps.any { map ->
                !action.availableFor(map)
            }
        }

    init {
        viewModelScope.launch {
            snapshotFlow { mapsViewModel.sharedMaps.isEmpty() }
                .collect { sharedMapsIsEmpty ->
                    availableSegments.clear()
                    availableSegments.addAll(MapsListSegment.entries.filter {
                        !(sharedMapsIsEmpty && it == MapsListSegment.SHARED)
                    })
                }
        }
    }

    fun getFilteredMaps(segment: MapsListSegment) = segment.getMaps(mapsViewModel)
        .filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
        .sortedWith { m1, m2 ->
            ListSorting.entries[prefs.mapsListViewOptions.sorting.value].comparator.compare(m1.file, m2.file)
        }
        .let {
            if (prefs.mapsListViewOptions.sortingReversed.value) it.reversed() else it
        }

    fun isMapSelected(map: MapFile): Boolean {
        return selectedMaps.any {
            it.path == map.path
        }
    }
}