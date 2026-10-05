package com.aliernfrog.pftool.ui.screen.maps

import androidx.compose.runtime.Composable
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.impl.mapActions
import com.aliernfrog.pftool.ui.component.SettingsButton
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import io.github.aliernfrog.pftool_shared.ui.screen.maps.MapDetailsScreen

@Composable
fun MapDetailsScreen(
    map: MapFile,
    vm: MapsViewModel,
    onNavigateSettingsRequest: () -> Unit,
    onNavigateBackRequest: (() -> Unit)?
) {
    MapDetailsScreen(
        map = map,
        mapActions = mapActions,
        showMapThumbnail = vm.prefs.showChosenMapThumbnail.value,
        showMapNameFieldGuide = vm.prefs.showMapNameFieldGuide.value,
        settingsButton = {
            SettingsButton { onNavigateSettingsRequest() }
        },
        onDismissMapNameFieldGuide = { vm.prefs.showMapNameFieldGuide.value = false },
        onViewThumbnailRequest = {
            vm.openMapThumbnailViewer(map)
        },
        onNavigateBackRequest = onNavigateBackRequest
    )
}