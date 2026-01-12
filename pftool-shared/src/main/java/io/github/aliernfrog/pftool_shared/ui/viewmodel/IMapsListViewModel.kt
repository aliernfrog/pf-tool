package io.github.aliernfrog.pftool_shared.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aliernfrog.pftool_shared.data.MapsListSegment
import io.github.aliernfrog.pftool_shared.impl.IMapFile
import io.github.aliernfrog.pftool_shared.repository.MapRepository
import kotlinx.coroutines.launch

class IMapsListViewModel(
    private val mapRepository: MapRepository
) : ViewModel() {
    val selectedMaps = mutableStateListOf<IMapFile>()
    val isLoading = mapRepository.isLoading

    fun reloadMaps(context: Context) {
        viewModelScope.launch {
            mapRepository.reloadMaps(context)
        }
    }

    fun getMapsForSegment(segment: MapsListSegment) = segment.getMaps(mapRepository)
}