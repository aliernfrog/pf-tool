package com.aliernfrog.pftool.ui.screen.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.impl.mapActions
import com.aliernfrog.pftool.ui.component.SettingsButton
import com.aliernfrog.pftool.ui.viewmodel.MapsListViewModel
import io.github.aliernfrog.pftool_shared.impl.FileWrapper
import io.github.aliernfrog.pftool_shared.ui.screen.maps.MapsListFileExtension
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
    @Suppress("UNCHECKED_CAST")
    MapsListScreen(
        title = title,
        supportedFileExtensions = listOf(
            MapsListFileExtension(
                extension = ".zip",
                mimeType = "application/zip"
            )
        ),
        mapsListSegments = vm.availableSegments,
        mapActions = mapActions,
        listViewOptions = vm.prefs.mapsListViewOptions,
        showThumbnailsInList = vm.prefs.showMapThumbnailsInList.value,
        showMultiSelectionActions = showMultiSelectionActions,
        multiSelectFloatingActionButton = { selectedMaps, clearSelection ->
            multiSelectFloatingActionButton(selectedMaps as List<MapFile>, clearSelection)
        },
        settingsButton = onNavigateSettingsRequest?.let { {
            SettingsButton(onClick = it)
        } },
        onBackClick = onBackClick,
        onMapPick = {
            onMapPick(when (it) {
                is MapFile -> it
                is FileWrapper -> MapFile(it)
                else -> MapFile(FileWrapper(it))
            })
        }
    )
}