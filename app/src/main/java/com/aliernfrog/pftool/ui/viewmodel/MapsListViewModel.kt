package com.aliernfrog.pftool.ui.viewmodel

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.pftool.util.manager.PreferenceManager
import io.github.aliernfrog.pftool_shared.data.MapsListSegment
import io.github.aliernfrog.pftool_shared.data.getDefaultMapsListSegments
import io.github.aliernfrog.pftool_shared.impl.IMapFile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MapsListViewModel(
    val prefs: PreferenceManager,
    private val mapsViewModel: MapsViewModel,
    context: Context
) : ViewModel() {
    val selectedMaps = mutableStateListOf<IMapFile>()
    val availableSegments = mutableStateListOf<MapsListSegment>()

    init {
        viewModelScope.launch {
            snapshotFlow { mapsViewModel.sharedMaps.isEmpty() }
                .collect { showSharedMapsSegment ->
                    availableSegments.clear()
                    availableSegments.addAll(getDefaultMapsListSegments(
                        includeSharedMapsSegment = showSharedMapsSegment,
                        reloadMaps = { mapsViewModel.loadMaps(context) },
                        getImportedMaps = { mapsViewModel.importedMaps },
                        getExportedMaps = { mapsViewModel.exportedMaps },
                        getSharedMaps = { mapsViewModel.sharedMaps }
                    ))
                }
        }
    }
}