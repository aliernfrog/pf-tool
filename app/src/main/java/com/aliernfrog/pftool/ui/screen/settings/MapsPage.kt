package com.aliernfrog.pftool.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.component.form.SwitchRow
import com.aliernfrog.pftool.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapsPage(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    SettingsPageContainer(
        title = stringResource(R.string.settings_maps),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        SwitchRow(
            title = stringResource(R.string.settings_maps_showChosenMapThumbnail),
            description = stringResource(R.string.settings_maps_showChosenMapThumbnail_description),
            checked = settingsViewModel.prefs.showChosenMapThumbnail
        ) {
            settingsViewModel.prefs.showChosenMapThumbnail = it
        }
        SwitchRow(
            title = stringResource(R.string.settings_maps_showMapThumbnailsInList),
            description = stringResource(R.string.settings_maps_showMapThumbnailsInList_description),
            checked = settingsViewModel.prefs.showMapThumbnailsInList
        ) {
            settingsViewModel.prefs.showMapThumbnailsInList = it
        }
    }
}