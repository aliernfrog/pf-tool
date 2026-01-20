package io.github.aliernfrog.pftool_shared.ui.screen.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSwitchRow
import io.github.aliernfrog.shared.ui.screen.settings.SettingsPageContainer
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import io.github.aliernfrog.shared.util.sharedStringResource

@Composable
fun MapsPage(
    showChosenMapThumbnailPref: BasePreferenceManager.Preference<Boolean>,
    showMapThumbnailsInListPref: BasePreferenceManager.Preference<Boolean>,
    stackupMapsPref: BasePreferenceManager.Preference<Boolean>,
    onNavigateBackRequest: () -> Unit
) {
    SettingsPageContainer(
        title = sharedStringResource(PFToolSharedString.SettingsMaps),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        ExpressiveSection(sharedStringResource(PFToolSharedString.SettingsMapsThumbnails)) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(PFToolSharedString.SettingsMapsThumbnailsChosen),
                        description = sharedStringResource(PFToolSharedString.SettingsMapsThumbnailsChosenDescription),
                        checked = showChosenMapThumbnailPref.value,
                        onCheckedChange = { showChosenMapThumbnailPref.value = it }
                    )
                },
                {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(PFToolSharedString.SettingsMapsThumbnailsList),
                        description = sharedStringResource(PFToolSharedString.SettingsMapsThumbnailsListDescription),
                        checked = showMapThumbnailsInListPref.value,
                        onCheckedChange = { showMapThumbnailsInListPref.value = it }
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        ExpressiveSection(sharedStringResource(PFToolSharedString.SettingsMapsBehavior)) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(PFToolSharedString.SettingsMapsBehaviorStackup),
                        description = sharedStringResource(PFToolSharedString.SettingsMapsBehaviorStackupDescription),
                        checked = stackupMapsPref.value,
                        onCheckedChange = { stackupMapsPref.value = it }
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}