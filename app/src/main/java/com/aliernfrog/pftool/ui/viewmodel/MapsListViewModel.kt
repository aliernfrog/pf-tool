package com.aliernfrog.pftool.ui.viewmodel

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aliernfrog.pftool.util.manager.PreferenceManager
import io.github.aliernfrog.pftool_shared.data.MapsListSegment
import io.github.aliernfrog.pftool_shared.data.getDefaultMapsListSegments
import io.github.aliernfrog.pftool_shared.repository.MapRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MapsListViewModel(
    val prefs: PreferenceManager,
    private val mapRepository: MapRepository
) : ViewModel() {
    val availableSegments = mutableStateListOf<MapsListSegment>()

    init {
        viewModelScope.launch {
            snapshotFlow { mapRepository.sharedMaps.asLiveData().value?.isEmpty() != true }
                .collect { showSharedMapsSegment ->
                    availableSegments.clear()
                    availableSegments.addAll(getDefaultMapsListSegments(
                        includeSharedMapsSegment = showSharedMapsSegment
                    ))
                }
        }
    }
}