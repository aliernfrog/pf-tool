package com.aliernfrog.pftool.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.data.PFMap
import com.aliernfrog.pftool.enum.MapsListSegment
import com.aliernfrog.pftool.enum.SortingOption
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState

class MapsListViewModel(
    val topToastState: TopToastState,
    val prefs: PreferenceManager,
    private val mapsViewModel: MapsViewModel
) : ViewModel() {

    var searchQuery by mutableStateOf("")
    var chosenSegment by mutableStateOf(MapsListSegment.IMPORTED)
    var sorting by mutableStateOf(SortingOption.ALPHABETICAL)
    var reverseList by mutableStateOf(false)

    /**
     * Map list with filters and sorting options applied.
     */
    val mapsToShow: List<PFMap>
        get() {
            val list = when (chosenSegment) {
                MapsListSegment.IMPORTED -> mapsViewModel.importedMaps
                MapsListSegment.EXPORTED -> mapsViewModel.exportedMaps
            }.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }.sortedWith(when (sorting) {
                SortingOption.ALPHABETICAL -> compareBy(PFMap::name)
                SortingOption.DATE -> compareByDescending(PFMap::lastModified)
                SortingOption.SIZE -> compareByDescending(PFMap::fileSize)
            })
            return if (reverseList) list.reversed() else list
        }
}