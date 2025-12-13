package io.github.aliernfrog.pftool_shared.ui.screen.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.pftool_shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.pftool_shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.pftool_shared.ui.component.expressive.ExpressiveSwitchRow
import io.github.aliernfrog.pftool_shared.util.SharedString
import io.github.aliernfrog.pftool_shared.util.manager.base.BasePreferenceManager
import io.github.aliernfrog.pftool_shared.util.sharedStringResource

@Composable
fun MapsPage(
    showChosenMapThumbnailPref: BasePreferenceManager.Preference<Boolean>,
    showMapThumbnailsInListPref: BasePreferenceManager.Preference<Boolean>,
    stackupMapsPref: BasePreferenceManager.Preference<Boolean>,
    onNavigateBackRequest: () -> Unit
) {
    SettingsPageContainer(
        title = sharedStringResource(SharedString.SETTINGS_MAPS),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        ExpressiveSection(sharedStringResource(SharedString.SETTINGS_MAPS_THUMBNAILS)) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(SharedString.SETTINGS_MAPS_THUMBNAILS_CHOSEN),
                        description = sharedStringResource(SharedString.SETTINGS_MAPS_THUMBNAILS_CHOSEN_DESCRIPTION),
                        checked = showChosenMapThumbnailPref.value,
                        onCheckedChange = { showChosenMapThumbnailPref.value = it }
                    )
                },
                {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(SharedString.SETTINGS_MAPS_THUMBNAILS_LIST),
                        description = sharedStringResource(SharedString.SETTINGS_MAPS_THUMBNAILS_LIST_DESCRIPTION),
                        checked = showMapThumbnailsInListPref.value,
                        onCheckedChange = { showMapThumbnailsInListPref.value = it }
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        ExpressiveSection(sharedStringResource(SharedString.SETTINGS_MAPS_THUMBNAILS_BEHAVIOR)) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(SharedString.SETTINGS_MAPS_THUMBNAILS_BEHAVIOR_STACKUP),
                        description = sharedStringResource(SharedString.SETTINGS_MAPS_THUMBNAILS_BEHAVIOR_STACKUP_DESCRIPTION),
                        checked = stackupMapsPref.value,
                        onCheckedChange = { stackupMapsPref.value = it }
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}