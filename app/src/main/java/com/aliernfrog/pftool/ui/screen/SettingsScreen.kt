package com.aliernfrog.pftool.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.SettingsConstant
import com.aliernfrog.pftool.data.ReleaseInfo
import com.aliernfrog.pftool.enum.FileManagementMethod
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.component.AppTopBar
import com.aliernfrog.pftool.ui.component.ButtonIcon
import com.aliernfrog.pftool.ui.component.RadioButtons
import com.aliernfrog.pftool.ui.component.form.ButtonRow
import com.aliernfrog.pftool.ui.component.form.ExpandableRow
import com.aliernfrog.pftool.ui.component.form.FormSection
import com.aliernfrog.pftool.ui.component.form.SwitchRow
import com.aliernfrog.pftool.ui.dialog.FolderConfigurationDialog
import com.aliernfrog.pftool.ui.sheet.LanguageSheet
import com.aliernfrog.pftool.ui.theme.AppComponentShape
import com.aliernfrog.pftool.ui.theme.Theme
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.pftool.ui.viewmodel.SettingsViewModel
import com.aliernfrog.pftool.util.staticutil.GeneralUtil
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val version = "${mainViewModel.applicationVersionName} (${mainViewModel.applicationVersionCode})"

    AppScaffold(
        topBar = { AppTopBar(
            title = stringResource(R.string.settings),
            scrollBehavior = it,
            onNavigationClick = onNavigateBackRequest
        ) },
        topAppBarState = settingsViewModel.topAppBarState
    ) {
        Column(Modifier.fillMaxSize().verticalScroll(settingsViewModel.scrollState)) {
            UpdateNotification(
                isShown = mainViewModel.updateAvailable,
                versionInfo = mainViewModel.latestVersionInfo,
                onClick = { scope.launch {
                    mainViewModel.updateSheetState.show()
                } }
            )
            
            // Appearance options
            FormSection(title = stringResource(R.string.settings_appearance)) {
                ExpandableRow(
                    expanded = settingsViewModel.themeOptionsExpanded,
                    title = stringResource(R.string.settings_appearance_theme),
                    description = stringResource(R.string.settings_appearance_theme_description),
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
                    checked = settingsViewModel.prefs.materialYou
                ) {
                    settingsViewModel.prefs.materialYou = it
                }
            }

            // General options
            FormSection(title = stringResource(R.string.settings_general)) {
                SwitchRow(
                    title = stringResource(R.string.settings_general_showChosenMapThumbnail),
                    description = stringResource(R.string.settings_general_showChosenMapThumbnail_description),
                    checked = settingsViewModel.prefs.showChosenMapThumbnail
                ) {
                    settingsViewModel.prefs.showChosenMapThumbnail = it
                }
                SwitchRow(
                    title = stringResource(R.string.settings_general_showMapThumbnailsInList),
                    description = stringResource(R.string.settings_general_showMapThumbnailsInList_description),
                    checked = settingsViewModel.prefs.showMapThumbnailsInList
                ) {
                    settingsViewModel.prefs.showMapThumbnailsInList = it
                }
                val enabledFileManagementMethod = FileManagementMethod.entries[settingsViewModel.prefs.fileManagementMethod]
                ButtonRow(
                    title = stringResource(R.string.settings_general_folders),
                    description = stringResource(R.string.settings_general_folders_description),
                    expanded = true,
                    arrowRotation = if (LocalLayoutDirection.current == LayoutDirection.Rtl) 270f else 90f
                ) {
                    settingsViewModel.foldersDialogShown = true
                }
                ExpandableRow(
                    expanded = settingsViewModel.fileServiceOptionsExpanded,
                    title = stringResource(R.string.settings_general_fileManagamentService),
                    trailingButtonText = stringResource(enabledFileManagementMethod.label),
                    onClickHeader = {
                        settingsViewModel.fileServiceOptionsExpanded = !settingsViewModel.fileServiceOptionsExpanded
                    }
                ) {
                    RadioButtons(
                        options = FileManagementMethod.entries.map { stringResource(it.label) },
                        selectedOptionIndex = enabledFileManagementMethod.ordinal,
                        onSelect = {
                            FileManagementMethod.entries[it].enable(settingsViewModel.prefs)
                        }
                    )
                }
                ButtonRow(
                    title = stringResource(R.string.settings_general_language),
                    description = stringResource(R.string.settings_general_language_description),
                    expanded = true,
                    arrowRotation = if (LocalLayoutDirection.current == LayoutDirection.Rtl) 270f else 90f
                ) { scope.launch {
                    settingsViewModel.languageSheetState.show()
                } }
            }

            // About app
            FormSection(title = stringResource(R.string.settings_about), bottomDivider = false) {
                ButtonRow(
                    title = stringResource(R.string.settings_about_version),
                    description = version,
                    trailingComponent = {
                        UpdateButton(
                            updateAvailable = mainViewModel.updateAvailable
                        ) { updateAvailable -> scope.launch {
                            if (updateAvailable) mainViewModel.updateSheetState.show()
                            else mainViewModel.checkUpdates(manuallyTriggered = true)
                        } }
                    }
                ) {
                    settingsViewModel.onAboutClick()
                }
                SwitchRow(
                    title = stringResource(R.string.settings_about_autoCheckUpdates),
                    description = stringResource(R.string.settings_about_autoCheckUpdates_description),
                    checked = settingsViewModel.prefs.autoCheckUpdates
                ) {
                    settingsViewModel.prefs.autoCheckUpdates = it
                }
                ExpandableRow(
                    expanded = settingsViewModel.linksExpanded,
                    title = stringResource(R.string.settings_about_links),
                    description = stringResource(R.string.settings_about_links_description),
                    onClickHeader = {
                        settingsViewModel.linksExpanded = !settingsViewModel.linksExpanded
                    }
                ) {
                    SettingsConstant.socials.forEach {
                        val icon = when(it.url.split("/")[2]) {
                            "discord.gg" -> painterResource(id = R.drawable.discord)
                            "github.com" -> painterResource(id = R.drawable.github)
                            "crowdin.com" -> rememberVectorPainter(Icons.Default.Translate)
                            else -> null
                        }
                        ButtonRow(
                            title = it.name,
                            description = it.url,
                            painter = icon,
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            uriHandler.openUri(it.url)
                        }
                    }
                }
            }

            // Experimental settings
            if (settingsViewModel.experimentalSettingsShown) FormSection(title = stringResource(R.string.settings_experimental), bottomDivider = false, topDivider = true) {
                Text(stringResource(R.string.settings_experimental_description), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp))
                SwitchRow(
                    title = stringResource(R.string.settings_experimental_showMaterialYouOption),
                    checked = settingsViewModel.showMaterialYouOption,
                    onCheckedChange = {
                        settingsViewModel.showMaterialYouOption = it
                    }
                )
                SwitchRow(
                    title = stringResource(R.string.settings_experimental_showMapNameFieldGuide),
                    checked = settingsViewModel.prefs.showMapNameFieldGuide,
                    onCheckedChange = {
                        settingsViewModel.prefs.showMapNameFieldGuide = it
                    }
                )
                ButtonRow(
                    title = stringResource(R.string.settings_experimental_checkUpdates)
                ) {
                    scope.launch {
                        mainViewModel.checkUpdates(ignoreVersion = true)
                    }
                }
                ButtonRow(
                    title = stringResource(R.string.settings_experimental_showUpdateToast)
                ) {
                    mainViewModel.showUpdateToast()
                }
                ButtonRow(
                    title = stringResource(R.string.settings_experimental_showUpdateDialog)
                ) {
                    scope.launch {
                        mainViewModel.updateSheetState.show()
                    }
                }
                SettingsConstant.experimentalPrefOptions.forEach { prefEdit ->
                    OutlinedTextField(
                        value = prefEdit.getValue(settingsViewModel.prefs),
                        onValueChange = {
                            prefEdit.setValue(it, settingsViewModel.prefs)
                        },
                        label = {
                            Text(stringResource(prefEdit.labelResourceId))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                ButtonRow(
                    title = stringResource(R.string.settings_experimental_resetPrefs),
                    contentColor = MaterialTheme.colorScheme.error
                ) {
                    SettingsConstant.experimentalPrefOptions.forEach {
                        it.setValue(it.default, settingsViewModel.prefs)
                    }
                    settingsViewModel.topToastState.showAndroidToast(
                        text = R.string.settings_experimental_resetPrefsDone,
                        icon = Icons.Rounded.Done
                    )
                    GeneralUtil.restartApp(context)
                }
            }
            Spacer(Modifier.navigationBarsPadding())
        }
    }

    LanguageSheet(
        sheetState = settingsViewModel.languageSheetState
    )

    if (settingsViewModel.foldersDialogShown) FolderConfigurationDialog(
        onDismissRequest = { settingsViewModel.foldersDialogShown = false }
    )
}

@Composable
fun UpdateNotification(
    isShown: Boolean,
    versionInfo: ReleaseInfo,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isShown,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            onClick = onClick,
            shape = AppComponentShape,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Update, contentDescription = null)
                Column {
                    Text(
                        text = stringResource(R.string.settings_updateNotification_updateAvailable)
                            .replace("{VERSION}", versionInfo.versionName),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.settings_updateNotification_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun UpdateButton(
    updateAvailable: Boolean,
    onClick: (updateAvailable: Boolean) -> Unit
) {
    AnimatedContent(updateAvailable) {
        if (it) ElevatedButton(
            onClick = { onClick(true) }
        ) {
            ButtonIcon(
                rememberVectorPainter(Icons.Default.Update)
            )
            Text(stringResource(R.string.settings_about_update))
        }
        else OutlinedButton(
            onClick = { onClick(false) }
        ) {
            Text(stringResource(R.string.settings_about_checkUpdates))
        }
    }
}