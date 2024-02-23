package com.aliernfrog.pftool.ui.screen.settings

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.SettingsConstant
import com.aliernfrog.pftool.data.ReleaseInfo
import com.aliernfrog.pftool.enum.StorageAccessType
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.component.AppSmallTopBar
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
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.pftool.ui.viewmodel.SettingsViewModel
import com.aliernfrog.pftool.util.extension.popBackStackSafe
import com.aliernfrog.pftool.util.staticutil.GeneralUtil
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onNavigateBackRequest: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SettingsPage.ROOT.id,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) + fadeIn()
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) + fadeOut()
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) + fadeIn()
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) + fadeOut()
        }
    ) {
        SettingsPage.entries.forEach { page ->
            composable(page.id) {
                page.content (
                    { navController.popBackStackSafe(onNoBackStack = onNavigateBackRequest) },
                    { navController.navigate(it.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsRootPage(
    onNavigateBackRequest: () -> Unit,
    onNavigateRequest: (SettingsPage) -> Unit
) {
    AppScaffold(
        topBar = { scrollBehavior ->
            AppTopBar(
                title = stringResource(R.string.settings),
                scrollBehavior = scrollBehavior,
                onNavigationClick = onNavigateBackRequest
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            SettingsPage.entries
                .filter { it != SettingsPage.ROOT }
                .forEach { page ->
                    ButtonRow(
                        title = stringResource(page.title),
                        description = stringResource(page.description),
                        painter = rememberVectorPainter(page.icon)
                    ) {
                        onNavigateRequest(page)
                    }
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPageContainer(
    title: String,
    onNavigateBackRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    AppScaffold(
        topBar = { scrollBehavior ->
            AppSmallTopBar(
                title = title,
                scrollBehavior = scrollBehavior,
                onNavigationClick = onNavigateBackRequest
            )
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OldSettingsScreen(
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

            // General options
            FormSection(title = stringResource(R.string.settings_general)) {
                val enabledStorageAccessType = StorageAccessType.entries[settingsViewModel.prefs.storageAccessType]
                ButtonRow(
                    title = stringResource(R.string.settings_storage_folders),
                    description = stringResource(R.string.settings_general_folders_description),
                    expanded = true,
                    arrowRotation = if (LocalLayoutDirection.current == LayoutDirection.Rtl) 270f else 90f
                ) {
                    settingsViewModel.foldersDialogShown = true
                }
                ExpandableRow(
                    expanded = settingsViewModel.fileServiceOptionsExpanded,
                    title = stringResource(R.string.settings_storage_storageAccessType),
                    trailingButtonText = stringResource(enabledStorageAccessType.label),
                    onClickHeader = {
                        settingsViewModel.fileServiceOptionsExpanded = !settingsViewModel.fileServiceOptionsExpanded
                    }
                ) {
                    RadioButtons(
                        options = StorageAccessType.entries.map { stringResource(it.label) },
                        selectedOptionIndex = enabledStorageAccessType.ordinal,
                        onSelect = {
                            StorageAccessType.entries[it].enable(settingsViewModel.prefs)
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

@Suppress("unused")
enum class SettingsPage(
    val id: String,
    @StringRes val title: Int,
    @StringRes val description: Int,
    val icon: ImageVector,
    val content: @Composable (
        onNavigateBackRequest: () -> Unit,
        onNavigateRequest: (SettingsPage) -> Unit
    ) -> Unit
) {
    ROOT(
        id = "root",
        title = R.string.settings,
        description = R.string.settings,
        icon = Icons.Outlined.Settings,
        content = { onNavigateBackRequest, onNavigateRequest ->
            SettingsRootPage(
                onNavigateBackRequest = onNavigateBackRequest,
                onNavigateRequest = onNavigateRequest
            )
        }
    ),

    APPEARANCE(
        id = "appearance",
        title = R.string.settings_appearance,
        description = R.string.settings_appearance_description,
        icon = Icons.Outlined.Palette,
        content = { onNavigateBackRequest, _ ->
            AppearancePage(onNavigateBackRequest = onNavigateBackRequest)
        }
    ),

    MAPS(
        id = "maps",
        title = R.string.settings_maps,
        description = R.string.settings_maps_description,
        icon = Icons.Outlined.PinDrop,
        content = { onNavigateBackRequest, _ ->
            MapsPage(onNavigateBackRequest = onNavigateBackRequest)
        }
    ),

    STORAGE(
        id = "files",
        title = R.string.settings_storage,
        description = R.string.settings_storage_description,
        icon = Icons.Outlined.FolderOpen,
        content = { onNavigateBackRequest, _ ->
            StoragePage(
                onNavigateBackRequest = onNavigateBackRequest
            )
        }
    ),

    LANGUAGE(
        id = "language",
        title = R.string.settings_language,
        description = R.string.settings_language_description,
        icon = Icons.Outlined.Translate,
        content = { _, _ ->
            TODO()
        }
    ),

    ABOUT(
        id = "about",
        title = R.string.settings_about,
        description = R.string.settings_about,
        icon = Icons.Outlined.Info,
        content = { onNavigateBackRequest, _ ->
            /* TODO */
            OldSettingsScreen {
                onNavigateBackRequest()
            }
        }
    )
}