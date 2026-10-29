package com.aliernfrog.pftool.ui.screen.maps

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.impl.mapActions
import com.aliernfrog.pftool.ui.component.SettingsButton
import com.aliernfrog.pftool.ui.viewmodel.MapsListViewModel
import io.github.aliernfrog.pftool_shared.data.FileExtension
import io.github.aliernfrog.pftool_shared.impl.FileWrapper
import io.github.aliernfrog.pftool_shared.ui.component.maps.MapsListFABMenu
import io.github.aliernfrog.pftool_shared.ui.screen.maps.MapsListScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapsListScreen(
    title: String = stringResource(R.string.mapsList_pickMap),
    vm: MapsListViewModel = koinViewModel(),
    showMultiSelectionActions: Boolean = true,
    multiSelectFloatingActionButton: @Composable (
        selectedMaps: List<MapFile>, clearSelection: () -> Unit
    ) -> Unit = { _, _ -> },
    onNavigateSettingsRequest: (() -> Unit)? = null,
    onBackClick: (() -> Unit)?,
    onMapPick: (MapFile) -> Unit
) {
    var addMapMenuExpanded by remember { mutableStateOf(false) }

    val mapPickHandler: (Any) -> Unit = {
        onMapPick(when (it) {
            is MapFile -> it
            is FileWrapper -> MapFile(it)
            else -> MapFile(FileWrapper(it))
        })
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        floatingActionButton = {
            MapsListFABMenu(
                expanded = addMapMenuExpanded,
                supportedFileExtensions = listOf(FileExtension(
                    extension = ".zip",
                    mimeType = "application/zip"
                )),
                onExpandedStateChange = { addMapMenuExpanded = it },
                onMapPick = mapPickHandler
            )
        }
    ) { _ ->
        @Suppress("UNCHECKED_CAST")
        MapsListScreen(
            title = title,
            mapsListSegments = vm.availableSegments,
            mapActions = mapActions,
            listViewOptions = vm.prefs.mapsListViewOptions,
            showThumbnailsInListPref = vm.prefs.showMapThumbnailsInList,
            dimList = addMapMenuExpanded,
            showMultiSelectionActions = showMultiSelectionActions,
            multiSelectFloatingActionButton = { selectedMaps, clearSelection ->
                multiSelectFloatingActionButton(selectedMaps as List<MapFile>, clearSelection)
            },
            settingsButton = onNavigateSettingsRequest?.let { {
                SettingsButton(onClick = it)
            } },
            onRemoveDimRequest = { addMapMenuExpanded = false },
            onBackClick = onBackClick,
            onMapPick = mapPickHandler
        )
    }
}