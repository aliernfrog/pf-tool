package com.aliernfrog.pftool.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.domain.MapsState
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.util.manager.PreferenceManager

class MapsListViewModel(
    val prefs: PreferenceManager,
    private val mapsState: MapsState
) : ViewModel() {
    val availableSegments
        get() = mapsState.availableSegments

    val selectedMaps = mutableStateListOf<MapFile>()
}