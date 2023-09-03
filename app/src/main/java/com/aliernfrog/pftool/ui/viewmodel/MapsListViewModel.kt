package com.aliernfrog.pftool.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.aliernfrog.pftool.data.PFMap
import com.aliernfrog.pftool.enum.MapType
import com.aliernfrog.pftool.enum.SortingOption
import com.aliernfrog.pftool.util.Destination
import com.aliernfrog.pftool.util.NavigationController
import com.aliernfrog.pftool.util.extension.navigate
import com.aliernfrog.pftool.util.extension.popBackStackSafe
import com.aliernfrog.pftool.util.extension.set
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.launch

class MapsListViewModel(
    val topToastState: TopToastState,
    val prefs: PreferenceManager,
    private val navigationController: NavigationController,
    private val mapsViewModel: MapsViewModel,
    context: Context
) : ViewModel() {
    val navController
        get() = navigationController.controller

    var onMapPick: (Any) -> Unit = {}
        private set

    var searchQuery by mutableStateOf("")
    var mapTypeFilter by mutableStateOf(MapType.IMPORTED)
    var sorting by mutableStateOf(SortingOption.ALPHABETICAL)
    var reverseList by mutableStateOf(false)

    init {
        viewModelScope.launch {
            mapsViewModel.loadMaps(context)
            onMapPick = {
                mapsViewModel.chooseMap(it)
                navController.set(Destination.MAPS)
            }
        }
    }

    val mapsToShow: List<PFMap>
        get() {
            val list = when (mapTypeFilter) {
                MapType.IMPORTED -> mapsViewModel.importedMaps
                MapType.EXPORTED -> mapsViewModel.exportedMaps
            }.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }.sortedWith(when (sorting) {
                SortingOption.ALPHABETICAL -> compareBy(PFMap::name)
                SortingOption.DATE -> compareByDescending(PFMap::lastModified)
                SortingOption.SIZE -> compareByDescending(PFMap::fileSize)
            })
            return if (reverseList) list.reversed() else list
        }

    fun showMapList(
        fallbackDestinationOnPick: Destination,
        navigate: (NavController) -> Unit = {
            it.navigate(Destination.MAPS_LIST)
        },
        onMapPick: (Any) -> Unit
    ) {
        this.onMapPick = {
            onMapPick(it)
            navController.popBackStackSafe(
                fallback = fallbackDestinationOnPick
            )
        }
        navigate(navController)
    }
}