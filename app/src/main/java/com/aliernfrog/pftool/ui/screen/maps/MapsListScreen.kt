package com.aliernfrog.pftool.ui.screen.maps

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import io.github.aliernfrog.shared.ui.component.util.AnimatedContentShadowWorkaround
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapsListScreen(
    title: String = stringResource(R.string.mapsList_pickMap),
    vm: MapsListViewModel = koinViewModel(),
    showMultiSelectionActions: Boolean = true,
    onNavigateSettingsRequest: (() -> Unit)? = null,
    onBackClick: (() -> Unit)?,
    onMapPick: (MapFile) -> Unit
) {
    val showAddMapFAB = vm.selectedMaps.isEmpty()
    var addMapMenuExpanded by remember { mutableStateOf(false) }

    val mapPickHandler: (Any) -> Unit = {
        onMapPick(when (it) {
            is MapFile -> it
            is FileWrapper -> MapFile(it)
            else -> MapFile(FileWrapper(it))
        })
    }

    BackHandler(enabled = addMapMenuExpanded) {
        addMapMenuExpanded = false
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        floatingActionButton = {
            AnimatedContentShadowWorkaround(showAddMapFAB) { showFAB ->
                if (showFAB) MapsListFABMenu(
                    expanded = addMapMenuExpanded,
                    supportedFileExtensions = listOf(FileExtension(
                        extension = ".zip",
                        mimeType = "application/zip"
                    )),
                    onExpandedStateChange = { addMapMenuExpanded = it },
                    onMapPick = mapPickHandler
                )
            }
        }
    ) { _ ->
        MapsListScreen(
            title = title,
            mapsListSegments = vm.availableSegments,
            mapActions = mapActions,
            selectedMaps = vm.selectedMaps,
            listViewOptions = vm.prefs.mapsListViewOptions,
            showThumbnailsInListPref = vm.prefs.showMapThumbnailsInList,
            showMultiSelectionActions = showMultiSelectionActions,
            settingsButton = onNavigateSettingsRequest?.let { {
                SettingsButton(onClick = it)
            } },
            onToggleMapSelectionRequest = { maps ->
                maps.forEach { map ->
                    val selected = vm.selectedMaps.any { it.path == map.path }
                    vm.selectedMaps.run {
                        if (selected) remove(map)
                        else add(map as MapFile)
                    }
                }
            },
            onClearMapSelectionRequest = {
                vm.selectedMaps.clear()
            },
            onBackClick = onBackClick,
            onMapPick = mapPickHandler
        )

        Crossfade(addMapMenuExpanded) {
            if (it) Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) {
                        addMapMenuExpanded = false
                    }
            )
        }
    }
}