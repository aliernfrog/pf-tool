package com.aliernfrog.pftool.ui.viewmodel

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.domain.MapsState
import com.aliernfrog.pftool.util.manager.PreferenceManager

@OptIn(ExperimentalMaterial3Api::class)
class MapsListViewModel(
    val prefs: PreferenceManager,
    private val mapsState: MapsState
) : ViewModel() {
    val availableSegments
        get() = mapsState.availableSegments
}