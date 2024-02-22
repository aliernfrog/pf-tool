package com.aliernfrog.pftool.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.component.AppSmallTopBar
import com.aliernfrog.pftool.ui.component.RadioButtons
import com.aliernfrog.pftool.ui.component.form.ExpandableRow
import com.aliernfrog.pftool.ui.component.form.SwitchRow
import com.aliernfrog.pftool.ui.theme.Theme
import com.aliernfrog.pftool.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearancePage(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    AppScaffold(
        topBar = { scrollBehavior ->
            AppSmallTopBar(
                title = stringResource(R.string.settings_appearance),
                scrollBehavior = scrollBehavior,
                onNavigationClick = onNavigateBackRequest
            )
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ExpandableRow(
                expanded = settingsViewModel.themeOptionsExpanded,
                title = stringResource(R.string.settings_appearance_theme),
                description = stringResource(R.string.settings_appearance_theme_description),
                painter = rememberVectorPainter(Icons.Outlined.DarkMode),
                trailingButtonText = stringResource(Theme.entries[settingsViewModel.prefs.theme].label),
                onClickHeader = {
                    settingsViewModel.themeOptionsExpanded = !settingsViewModel.themeOptionsExpanded
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
}