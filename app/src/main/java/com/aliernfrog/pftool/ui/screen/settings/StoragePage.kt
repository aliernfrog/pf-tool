package com.aliernfrog.pftool.ui.screen.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.SettingsConstant
import com.aliernfrog.pftool.data.PrefEditItem
import com.aliernfrog.pftool.enum.StorageAccessType
import com.aliernfrog.pftool.enum.isCompatible
import com.aliernfrog.pftool.externalStorageRoot
import com.aliernfrog.pftool.folderPickerSupportsInitialUri
import com.aliernfrog.pftool.ui.component.VerticalSegmentor
import com.aliernfrog.pftool.ui.component.expressive.ExpressiveRowHeader
import com.aliernfrog.pftool.ui.component.expressive.ExpressiveRowIcon
import com.aliernfrog.pftool.ui.component.expressive.ExpressiveSection
import com.aliernfrog.pftool.ui.component.expressive.toRowFriendlyColor
import com.aliernfrog.pftool.ui.component.form.ExpandableRow
import com.aliernfrog.pftool.ui.theme.AppComponentShape
import com.aliernfrog.pftool.ui.viewmodel.SettingsViewModel
import com.aliernfrog.pftool.util.extension.horizontalFadingEdge
import com.aliernfrog.pftool.util.extension.resolveString
import com.aliernfrog.pftool.util.extension.takePersistablePermissions
import com.aliernfrog.pftool.util.extension.toPath
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.pftool.util.manager.base.BasePreferenceManager
import com.aliernfrog.pftool.util.staticutil.FileUtil
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun StoragePage(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    var storageAccessTypesExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val selectedStorageAccessType = StorageAccessType.entries[settingsViewModel.prefs.storageAccessType.value]

    SettingsPageContainer(
        title = stringResource(R.string.settings_storage),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        VerticalSegmentor(
            {
                ExpandableRow(
                    expanded = storageAccessTypesExpanded,
                    title = stringResource(R.string.settings_storage_storageAccessType),
                    description = stringResource(selectedStorageAccessType.label),
                    icon = {
                        ExpressiveRowIcon(
                            painter = rememberVectorPainter(Icons.Outlined.FolderOpen),
                            containerColor = Color.Blue.toRowFriendlyColor
                        )
                    },
                    onClickHeader = { storageAccessTypesExpanded = !storageAccessTypesExpanded }
                ) {
                    val choices: List<@Composable () -> Unit> = StorageAccessType.entries.filter { it.isCompatible() }.map { type -> {
                        val selected = settingsViewModel.prefs.storageAccessType.value == type.ordinal
                        fun onSelect() {
                            if (!selected) type.enable(settingsViewModel.prefs)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = ::onSelect)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = ::onSelect
                            )
                            ExpressiveRowHeader(
                                title = stringResource(type.label),
                                description = stringResource(type.description)
                            )
                        }
                    } }
                    VerticalSegmentor(
                        *choices.toTypedArray(),
                        modifier = Modifier.padding(
                            start = 12.dp,
                            end = 12.dp,
                            top = 4.dp,
                            bottom = 8.dp
                        )
                    )
                }
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        ExpressiveSection(stringResource(R.string.settings_storage_folders)) {
            FolderConfiguration(
                useRawPathInputs = selectedStorageAccessType != StorageAccessType.SAF
            )
        }
    }
}

@Composable
private fun FolderConfiguration(
    prefs: PreferenceManager = koinInject(),
    useRawPathInputs: Boolean
) {
    val context = LocalContext.current
    var activePref: PrefEditItem<String>? = remember { null }
    val openFolderLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree(), onResult = { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val pref = activePref ?: return@rememberLauncherForActivityResult
        if (!useRawPathInputs) uri.takePersistablePermissions(context)
        pref.preference(prefs).value = uri.toString().let {
            if (useRawPathInputs) FileUtil.getFilePath(it) else it
        }
    })

    AnimatedContent(useRawPathInputs) { rawPathInput ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsConstant.folders.forEach { prefEditItem ->
                val pref = prefEditItem.preference(prefs)
                val label = prefEditItem.label(prefs).resolveString()
                if (rawPathInput) RawPathInput(
                    label = label,
                    pref = pref,
                    onPickFolderRequest = {
                        activePref = prefEditItem
                        openFolderLauncher.launch(null)
                    }
                )
                else FolderCard(
                    label = label,
                    pref = pref,
                    path = getFolderDescription(pref.value),
                    onPickFolderRequest = { uri ->
                        activePref = prefEditItem
                        openFolderLauncher.launch(uri)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RawPathInput(
    label: String,
    pref: BasePreferenceManager.Preference<String>,
    onPickFolderRequest: () -> Unit
) {
    val currentPath = pref.value
    val isDefault = pref.defaultValue == currentPath
    OutlinedTextField(
        value = currentPath,
        onValueChange = {
            pref.value = it
        },
        label = {
            Text(label)
        },
        supportingText = if (!isDefault) { {
            Text(stringResource(R.string.settings_storage_folders_default).replace("%s", pref.defaultValue))
        } } else null,
        trailingIcon = {
            Row {
                Crossfade(!isDefault) { enabled ->
                    IconButton(
                        onClick = { pref.value = pref.defaultValue },
                        shapes = IconButtonDefaults.shapes(),
                        enabled = enabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = stringResource(R.string.settings_storage_folders_restoreDefault)
                        )
                    }
                }
                IconButton(
                    shapes = IconButtonDefaults.shapes(),
                    onClick = { onPickFolderRequest() }
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = stringResource(R.string.settings_storage_folders_choose)
                    )
                }
            }
        },
        singleLine = true,
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    )
}

@Composable
fun FolderCard(
    label: String,
    pref: BasePreferenceManager.Preference<String>,
    path: String,
    onPickFolderRequest: (Uri?) -> Unit
) {
    val buttonsScrollState = rememberScrollState()
    val recommendedPath = remember { pref.defaultValue.removePrefix(externalStorageRoot) }
    val dataFolderPath = remember { externalStorageRoot+"Android/data" }
    val usingRecommendedPath = path.equals(recommendedPath, ignoreCase = true)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = AppComponentShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = path,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 13.sp
            )

            if (!usingRecommendedPath) {
                Text(
                    text = stringResource(R.string.settings_storage_folders_recommendedFolder),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = recommendedPath,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp
                )
            }

            Row(
                modifier = Modifier
                    .horizontalFadingEdge(
                        scrollState = buttonsScrollState,
                        edgeColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        isRTL = LocalLayoutDirection.current == LayoutDirection.Rtl
                    )
                    .horizontalScroll(buttonsScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (folderPickerSupportsInitialUri) AssistChip(
                    onClick = {
                        onPickFolderRequest(FileUtil.getUriForPath(pref.defaultValue))
                    },
                    label = {
                        Text(stringResource(R.string.settings_storage_folders_openRecommended))
                    }
                )

                AssistChip(
                    onClick = { onPickFolderRequest(null) },
                    label = {
                        Text(stringResource(R.string.settings_storage_folders_choose))
                    }
                )

                if (folderPickerSupportsInitialUri) AssistChip(
                    onClick = {
                        onPickFolderRequest(FileUtil.getUriForPath(dataFolderPath))
                    },
                    label = {
                        Text(stringResource(R.string.settings_storage_folders_openAndroidData))
                    }
                )
            }
        }
    }
}

@Composable
private fun getFolderDescription(path: String): String {
    var text = path
    if (text.isNotEmpty()) try {
        text = text.toUri().toPath().removePrefix(externalStorageRoot)
    } catch (_: Exception) {}
    return text.ifEmpty { stringResource(R.string.settings_storage_folders_notSet) }
}