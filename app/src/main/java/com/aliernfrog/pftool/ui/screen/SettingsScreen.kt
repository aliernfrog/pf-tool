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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.SettingsConstant
import com.aliernfrog.pftool.data.ReleaseInfo
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.component.ButtonIcon
import com.aliernfrog.pftool.ui.component.RadioButtons
import com.aliernfrog.pftool.ui.component.form.ButtonRow
import com.aliernfrog.pftool.ui.component.form.ExpandableRow
import com.aliernfrog.pftool.ui.component.form.FormSection
import com.aliernfrog.pftool.ui.component.form.SwitchRow
import com.aliernfrog.pftool.ui.component.settings.FolderConfiguration
import com.aliernfrog.pftool.ui.theme.AppComponentShape
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.pftool.ui.viewmodel.SettingsViewModel
import com.aliernfrog.pftool.util.staticutil.GeneralUtil
import com.aliernfrog.toptoast.enum.TopToastType
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel = getViewModel(),
    settingsViewModel: SettingsViewModel = getViewModel()
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val version = "${mainViewModel.applicationVersionName} (${mainViewModel.applicationVersionCode})"

    AppScaffold(
        title = stringResource(R.string.settings),
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
                    onClickHeader = {
                        settingsViewModel.themeOptionsExpanded = !settingsViewModel.themeOptionsExpanded
                    }
                ) {
                    RadioButtons(
                        options = listOf(
                            stringResource(R.string.settings_appearance_theme_system),
                            stringResource(R.string.settings_appearance_theme_light),
                            stringResource(R.string.settings_appearance_theme_dark)
                        ),
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
                    title = stringResource(R.string.settings_general_showMapThumbnailsInList),
                    description = stringResource(R.string.settings_general_showMapThumbnailsInList_description),
                    checked = settingsViewModel.prefs.showMapThumbnailsInList
                ) {
                    settingsViewModel.prefs.showMapThumbnailsInList = it
                }
                ExpandableRow(
                    expanded = settingsViewModel.folderOptionsExpanded,
                    title = stringResource(R.string.settings_general_folders),
                    description = stringResource(R.string.settings_general_folders_description),
                    onClickHeader = {
                        settingsViewModel.folderOptionsExpanded = !settingsViewModel.folderOptionsExpanded
                    }
                ) {
                    FolderConfiguration()
                }
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
                    var value by remember { mutableStateOf(
                        settingsViewModel.prefs.getString(prefEdit.key, prefEdit.default)
                    ) }
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            value = it
                            settingsViewModel.prefs.putString(prefEdit.key, value)
                        },
                        label = {
                            Text("Prefs: ${prefEdit.key}")
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
                        settingsViewModel.prefs.putString(it.key, it.default)
                    }
                    settingsViewModel.topToastState.showToast(
                        text = R.string.settings_experimental_resetPrefsDone,
                        icon = Icons.Rounded.Done,
                        type = TopToastType.ANDROID
                    )
                    GeneralUtil.restartApp(context)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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