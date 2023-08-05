package com.aliernfrog.pftool.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.aliernfrog.pftool.ui.component.ButtonShapeless
import com.aliernfrog.pftool.ui.component.ButtonWithComponent
import com.aliernfrog.pftool.ui.component.ColumnDivider
import com.aliernfrog.pftool.ui.component.ColumnRounded
import com.aliernfrog.pftool.ui.component.RadioButtons
import com.aliernfrog.pftool.ui.component.Switch
import com.aliernfrog.pftool.ui.component.TextField
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
            ColumnDivider(title = stringResource(R.string.settings_appearance)) {
                ButtonShapeless(
                    title = stringResource(R.string.settings_appearance_theme),
                    description = stringResource(R.string.settings_appearance_theme_description),
                    expanded = settingsViewModel.themeOptionsExpanded
                ) {
                    settingsViewModel.themeOptionsExpanded = !settingsViewModel.themeOptionsExpanded
                }
                AnimatedVisibility(
                    visible = settingsViewModel.themeOptionsExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    ColumnRounded(Modifier.padding(horizontal = 8.dp)) {
                        RadioButtons(
                            options = listOf(
                                stringResource(R.string.settings_appearance_theme_system),
                                stringResource(R.string.settings_appearance_theme_light),
                                stringResource(R.string.settings_appearance_theme_dark)
                            ),
                            initialIndex = settingsViewModel.prefs.theme,
                            optionsRounded = true
                        ) {
                            settingsViewModel.prefs.theme = it
                        }
                    }
                }
                if (settingsViewModel.showMaterialYouOption) Switch(
                    title = stringResource(R.string.settings_appearance_materialYou),
                    description = stringResource(R.string.settings_appearance_materialYou_description),
                    checked = settingsViewModel.prefs.materialYou
                ) {
                    settingsViewModel.prefs.materialYou = it
                }
            }

            // Maps options
            ColumnDivider(title = stringResource(R.string.settings_maps)) {
                Switch(
                    title = stringResource(R.string.settings_maps_showMapThumbnailsInList),
                    description = stringResource(R.string.settings_maps_showMapThumbnailsInList_description),
                    checked = settingsViewModel.prefs.showMapThumbnailsInList
                ) {
                    settingsViewModel.prefs.showMapThumbnailsInList = it
                }
            }

            // About app
            ColumnDivider(title = stringResource(R.string.settings_about), bottomDivider = false) {
                ButtonWithComponent(
                    title = stringResource(R.string.settings_about_version),
                    description = version,
                    component = {
                        OutlinedButton(
                            onClick = { scope.launch {
                                mainViewModel.checkUpdates(manuallyTriggered = true)
                            } }
                        ) {
                            Text(stringResource(R.string.settings_about_checkUpdates))
                        }
                    }
                ) {
                    settingsViewModel.onAboutClick()
                }
                Switch(
                    title = stringResource(R.string.settings_about_autoCheckUpdates),
                    description = stringResource(R.string.settings_about_autoCheckUpdates_description),
                    checked = settingsViewModel.prefs.autoCheckUpdates
                ) {
                    settingsViewModel.prefs.autoCheckUpdates = it
                }
                ButtonShapeless(
                    title = stringResource(R.string.settings_about_links),
                    description = stringResource(R.string.settings_about_links_description),
                    expanded = settingsViewModel.linksExpanded
                ) {
                    settingsViewModel.linksExpanded = !settingsViewModel.linksExpanded
                }
                AnimatedVisibility(
                    visible = settingsViewModel.linksExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    ColumnRounded(Modifier.padding(horizontal = 8.dp)) {
                        SettingsConstant.socials.forEach {
                            val icon = when(it.url.split("/")[2]) {
                                "discord.gg" -> painterResource(id = R.drawable.discord)
                                "github.com" -> painterResource(id = R.drawable.github)
                                else -> null
                            }
                            ButtonShapeless(
                                title = it.name,
                                painter = icon,
                                rounded = true,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ) {
                                uriHandler.openUri(it.url)
                            }
                        }
                    }
                }
            }

            // Experimental settings
            if (settingsViewModel.experimentalSettingsShown) ColumnDivider(title = stringResource(R.string.settings_experimental), bottomDivider = false, topDivider = true) {
                Text(stringResource(R.string.settings_experimental_description), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp))
                Switch(
                    title = stringResource(R.string.settings_experimental_showMaterialYouOption),
                    checked = settingsViewModel.showMaterialYouOption,
                    onCheckedChange = {
                        settingsViewModel.showMaterialYouOption = it
                    }
                )
                ButtonShapeless(title = stringResource(R.string.settings_experimental_checkUpdates)) {
                    scope.launch {
                        mainViewModel.checkUpdates(ignoreVersion = true)
                    }
                }
                ButtonShapeless(title = stringResource(R.string.settings_experimental_showUpdateToast)) {
                    mainViewModel.showUpdateToast()
                }
                ButtonShapeless(title = stringResource(R.string.settings_experimental_showUpdateDialog)) {
                    scope.launch {
                        mainViewModel.updateSheetState.show()
                    }
                }
                SettingsConstant.experimentalPrefOptions.forEach { prefEdit ->
                    val value = remember { mutableStateOf(
                        settingsViewModel.prefs.getString(prefEdit.key, prefEdit.default)
                    ) }
                    TextField(label = { Text(text = "Prefs: ${prefEdit.key}") }, value = value.value, modifier = Modifier.padding(horizontal = 8.dp),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surface,
                        rounded = false,
                        onValueChange = {
                            value.value = it
                            settingsViewModel.prefs.putString(prefEdit.key, it)
                        }
                    )
                }
                ButtonShapeless(title = stringResource(R.string.settings_experimental_resetPrefs), contentColor = MaterialTheme.colorScheme.error) {
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