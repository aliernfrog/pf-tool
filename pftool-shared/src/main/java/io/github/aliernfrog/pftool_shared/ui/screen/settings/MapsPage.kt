package io.github.aliernfrog.pftool_shared.ui.screen.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.pftool_shared.util.sharedStringResource
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSwitchRow
import io.github.aliernfrog.shared.ui.screen.settings.SettingsPageContainer
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager

@Composable
fun MapsPage(
    showChosenMapThumbnailPref: BasePreferenceManager.Preference<Boolean>,
    showMapThumbnailsInListPref: BasePreferenceManager.Preference<Boolean>,
    stackupMapsPref: BasePreferenceManager.Preference<Boolean>,
    onNavigateBackRequest: () -> Unit
) {
    SettingsPageContainer(
        title = sharedStringResource(PFToolSharedString::settingsMaps),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        ExpressiveSection(sharedStringResource(PFToolSharedString::settingsMapsThumbnails)) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(PFToolSharedString::settingsMapsThumbnailsChosen),
                        description = sharedStringResource(PFToolSharedString::settingsMapsThumbnailsChosenDescription),
                        checked = showChosenMapThumbnailPref.value,
                        onCheckedChange = { showChosenMapThumbnailPref.value = it }
                    )
                },
                {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(PFToolSharedString::settingsMapsThumbnailsList),
                        description = sharedStringResource(PFToolSharedString::settingsMapsThumbnailsListDescription),
                        checked = showMapThumbnailsInListPref.value,
                        onCheckedChange = { showMapThumbnailsInListPref.value = it }
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        ExpressiveSection(sharedStringResource(PFToolSharedString::settingsMapsBehavior)) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(PFToolSharedString::settingsMapsBehaviorStackup),
                        description = sharedStringResource(PFToolSharedString::settingsMapsBehaviorStackupDescription),
                        checked = stackupMapsPref.value,
                        onCheckedChange = { stackupMapsPref.value = it }
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}