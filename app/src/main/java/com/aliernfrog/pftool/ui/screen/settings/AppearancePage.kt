package com.aliernfrog.pftool.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.component.RadioButtons
import com.aliernfrog.pftool.ui.component.form.ExpandableRow
import com.aliernfrog.pftool.ui.component.form.SwitchRow
import com.aliernfrog.pftool.ui.theme.Theme
import com.aliernfrog.pftool.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppearancePage(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    var themeOptionsExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    SettingsPageContainer(
        title = stringResource(R.string.settings_appearance),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        ExpandableRow(
            expanded = themeOptionsExpanded,
            title = stringResource(R.string.settings_appearance_theme),
            description = stringResource(R.string.settings_appearance_theme_description),
            painter = rememberVectorPainter(Icons.Outlined.DarkMode),
            trailingButtonText = stringResource(Theme.entries[settingsViewModel.prefs.theme].label),
            onClickHeader = {
                themeOptionsExpanded = !themeOptionsExpanded
            }
        ) {
            RadioButtons(
                options = Theme.entries.map { stringResource(it.label) },
                selectedOptionIndex = settingsViewModel.prefs.theme
            ) {
                settingsViewModel.prefs.theme = it
            }
        }
        if (settingsViewModel.showMaterialYouOption) SwitchRow(
            title = stringResource(R.string.settings_appearance_materialYou),
            description = stringResource(R.string.settings_appearance_materialYou_description),
            painter = rememberVectorPainter(Icons.Outlined.Brush),
            checked = settingsViewModel.prefs.materialYou
        ) {
            settingsViewModel.prefs.materialYou = it
        }
    }
}