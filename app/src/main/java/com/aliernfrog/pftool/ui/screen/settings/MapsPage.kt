package com.aliernfrog.pftool.ui.screen.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.component.VerticalSegmentor
import com.aliernfrog.pftool.ui.component.expressive.ExpressiveSection
import com.aliernfrog.pftool.ui.component.expressive.ExpressiveSwitchRow
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
        ExpressiveSection(stringResource(R.string.settings_maps_thumbnails)) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = stringResource(R.string.settings_maps_thumbnails_chosen),
                        description = stringResource(R.string.settings_maps_thumbnails_chosen_description),
                        checked = settingsViewModel.prefs.showChosenMapThumbnail.value
                    ) {
                        settingsViewModel.prefs.showChosenMapThumbnail.value = it
                    }
                },
                {
                    ExpressiveSwitchRow(
                        title = stringResource(R.string.settings_maps_thumbnails_list),
                        description = stringResource(R.string.settings_maps_thumbnails_list_description),
                        checked = settingsViewModel.prefs.showMapThumbnailsInList.value
                    ) {
                        settingsViewModel.prefs.showMapThumbnailsInList.value = it
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        ExpressiveSection(stringResource(R.string.settings_maps_behavior)) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = stringResource(R.string.settings_maps_behavior_stackup),
                        description = stringResource(R.string.settings_maps_behavior_stackup_description),
                        checked = settingsViewModel.prefs.stackupMaps.value
                    ) {
                        settingsViewModel.prefs.stackupMaps.value = it
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}