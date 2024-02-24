package com.aliernfrog.pftool.ui.screen.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.SettingsConstant
import com.aliernfrog.pftool.data.PrefEditItem
import com.aliernfrog.pftool.enum.StorageAccessType
import com.aliernfrog.pftool.externalStorageRoot
import com.aliernfrog.pftool.folderPickerSupportsInitialUri
import com.aliernfrog.pftool.ui.component.FadeVisibility
import com.aliernfrog.pftool.ui.component.form.DividerRow
import com.aliernfrog.pftool.ui.component.form.ExpandableRow
import com.aliernfrog.pftool.ui.component.form.FormHeader
import com.aliernfrog.pftool.ui.component.form.FormSection
import com.aliernfrog.pftool.ui.theme.AppComponentShape
import com.aliernfrog.pftool.ui.viewmodel.SettingsViewModel
import com.aliernfrog.pftool.util.extension.horizontalFadingEdge
import com.aliernfrog.pftool.util.extension.takePersistablePermissions
import com.aliernfrog.pftool.util.extension.toPath
import com.aliernfrog.pftool.util.manager.PreferenceManager
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

    val selectedStorageAccessType = StorageAccessType.entries[settingsViewModel.prefs.storageAccessType]

    SettingsPageContainer(
        title = stringResource(R.string.settings_storage),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        ExpandableRow(
            expanded = storageAccessTypesExpanded,
            title = stringResource(R.string.settings_storage_storageAccessType),
            painter = rememberVectorPainter(Icons.Outlined.FolderOpen),
            trailingButtonText = stringResource(selectedStorageAccessType.label),
            onClickHeader = { storageAccessTypesExpanded = !storageAccessTypesExpanded }
        ) {
            StorageAccessType.entries.forEachIndexed { index, type ->
                val selected = settingsViewModel.prefs.storageAccessType == type.ordinal
                fun onSelect() {
                    StorageAccessType.entries[index].enable(settingsViewModel.prefs)
                }
                if (index != 0) DividerRow()
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
                    FormHeader(
                        title = stringResource(type.label),
                        description = stringResource(type.description)
                    )
                }
            }
        }
        FormSection(
            title = stringResource(R.string.settings_storage_folders),
            topDivider = true,
            bottomDivider = false
        ) {
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
    var activePref: PrefEditItem? = remember { null }
    val openFolderLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree(), onResult = {
        if (it == null) return@rememberLauncherForActivityResult
        val pref = activePref ?: return@rememberLauncherForActivityResult
        it.takePersistablePermissions(context)
        pref.setValue(it.toString(), prefs)
    })

    AnimatedContent(useRawPathInputs) { rawPathInput ->
        Column {
            SettingsConstant.folders.forEach { pref ->
                if (rawPathInput) RawPathInput(
                    pref = pref,
                    prefs = prefs,
                    onPickFolderRequest = {
                        openFolderLauncher.launch(null)
                    }
                )
                else FolderCard(
                    pref = pref,
                    path = getFolderDescription(
                        folder = pref,
                        prefs = prefs
                    ),
                    onPickFolderRequest = { uri ->
                        activePref = pref
                        openFolderLauncher.launch(uri)
                    }
                )
            }
        }
    }
}

@Composable
fun RawPathInput(
    pref: PrefEditItem,
    prefs: PreferenceManager,
    onPickFolderRequest: () -> Unit
) {
    val currentPath = pref.getValue(prefs)
    val isDefault = pref.default == currentPath
    OutlinedTextField(
        value = currentPath,
        onValueChange = {
            pref.setValue(it, prefs)
        },
        label = {
            Text(stringResource(pref.labelResourceId))
        },
        supportingText = {
            FadeVisibility(!isDefault) {
                Text(
                    stringResource(R.string.settings_storage_folders_default).replace("%s", pref.default)
                )
            }
        },
        trailingIcon = {
            Row {
                Crossfade(!isDefault) { enabled ->
                    IconButton(
                        onClick = { pref.setValue(pref.default, prefs) },
                        enabled = enabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = stringResource(R.string.settings_storage_folders_restoreDefault)
                        )
                    }
                }
                IconButton(
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
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
fun FolderCard(
    pref: PrefEditItem,
    path: String,
    onPickFolderRequest: (Uri?) -> Unit
) {
    val buttonsScrollState = rememberScrollState()
    val recommendedPath = remember { pref.default.removePrefix(externalStorageRoot) }
    val dataFolderPath = remember { externalStorageRoot+"Android/data" }
    val usingRecommendedPath = path.equals(recommendedPath, ignoreCase = true)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = AppComponentShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text(
                text = stringResource(pref.labelResourceId),
                style = MaterialTheme.typography.titleMedium
            )
            Text(path)

            if (!usingRecommendedPath) {
                Text(
                    text = stringResource(R.string.settings_storage_folders_recommendedFolder),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(recommendedPath)
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
                        onPickFolderRequest(FileUtil.getUriForPath(pref.default))
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
private fun getFolderDescription(
    folder: PrefEditItem,
    prefs: PreferenceManager
): String {
    var text = folder.getValue(prefs)
    if (text.isNotEmpty()) try {
        text = Uri.parse(text).toPath()?.removePrefix(externalStorageRoot)
            ?: text
    } catch (_: Exception) {}
    return text.ifEmpty { stringResource(R.string.settings_storage_folders_notSet) }
}