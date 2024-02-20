package com.aliernfrog.pftool.ui.dialog

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.SettingsConstant
import com.aliernfrog.pftool.data.PrefEditItem
import com.aliernfrog.pftool.enum.FileManagementMethod
import com.aliernfrog.pftool.externalStorageRoot
import com.aliernfrog.pftool.filesAppMightBlockAndroidData
import com.aliernfrog.pftool.folderPickerSupportsInitialUri
import com.aliernfrog.pftool.ui.component.FadeVisibility
import com.aliernfrog.pftool.ui.component.FilesDowngradeNotice
import com.aliernfrog.pftool.ui.component.form.DividerRow
import com.aliernfrog.pftool.ui.viewmodel.SettingsViewModel
import com.aliernfrog.pftool.util.extension.horizontalFadingEdge
import com.aliernfrog.pftool.util.extension.toPath
import com.aliernfrog.pftool.util.extension.takePersistablePermissions
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.pftool.util.staticutil.FileUtil
import org.koin.androidx.compose.koinViewModel

@Composable
fun FolderConfigurationDialog(
    onDismissRequest: () -> Unit,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val isSAF = settingsViewModel.prefs.fileManagementMethod == FileManagementMethod.SAF.ordinal
    val folders = remember { SettingsConstant.folders }
    var activePref: PrefEditItem? = remember { null }
    val openFolderLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree(), onResult = {
        if (it == null) return@rememberLauncherForActivityResult
        val pref = activePref ?: return@rememberLauncherForActivityResult
        it.takePersistablePermissions(context)
        pref.setValue(it.toString(), settingsViewModel.prefs)
    })

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.action_done))
            }
        },
        title = {
            Text(stringResource(R.string.settings_general_folders))
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isSAF && filesAppMightBlockAndroidData) FilesDowngradeNotice()
                folders.forEachIndexed { index, pref ->
                    if (isSAF) FolderCard(
                        pref = pref,
                        path = getFolderDescription(
                            folder = pref,
                            prefs = settingsViewModel.prefs
                        ),
                        showTopDivider = index > 0,
                        onPickFolderRequest = { uri ->
                            activePref = pref
                            openFolderLauncher.launch(uri)
                        }
                    )
                    else RawPathInput(
                        pref = pref,
                        prefs = settingsViewModel.prefs,
                        onPickFolderRequest = {
                            openFolderLauncher.launch(null)
                        }
                    )
                }
            }
        }
    )
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
                    stringResource(R.string.settings_general_folders_default).replace("%s", pref.default)
                )
            }
        },
        leadingIcon = {
            IconButton(
                onClick = { onPickFolderRequest() }
            ) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = stringResource(R.string.settings_general_folders_choose)
                )
            }
        },
        trailingIcon = if (isDefault) null else { {
            IconButton(
                onClick = { pref.setValue(pref.default, prefs) }
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = stringResource(R.string.settings_general_folders_restoreDefault)
                )
            }
        } },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun FolderCard(
    pref: PrefEditItem,
    path: String,
    showTopDivider: Boolean,
    onPickFolderRequest: (Uri?) -> Unit
) {
    val buttonsScrollState = rememberScrollState()
    val recommendedPath = remember { pref.default.removePrefix(externalStorageRoot) }
    val dataFolderPath = remember { externalStorageRoot+"Android/data" }
    val usingRecommendedPath = path.equals(recommendedPath, ignoreCase = true)
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (showTopDivider) DividerRow(
            modifier = Modifier.padding(bottom = 10.dp),
            alpha = 1f
        )

        Text(
            text = stringResource(pref.labelResourceId),
            style = MaterialTheme.typography.titleMedium
        )
        Text(path)

        if (!usingRecommendedPath) {
            Text(
                text = stringResource(R.string.settings_general_folders_recommendedFolder),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(recommendedPath)
        }

        Box(
            modifier = Modifier
                .horizontalFadingEdge(
                    scrollState = buttonsScrollState,
                    edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                    isRTL = LocalLayoutDirection.current == LayoutDirection.Rtl
                )
        ) {
            Row(
                modifier = Modifier.horizontalScroll(buttonsScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (folderPickerSupportsInitialUri) AssistChip(
                    onClick = {
                        onPickFolderRequest(FileUtil.getUriForPath(pref.default))
                    },
                    label = {
                        Text(stringResource(R.string.settings_general_folders_openRecommended))
                    }
                )

                AssistChip(
                    onClick = { onPickFolderRequest(null) },
                    label = {
                        Text(stringResource(R.string.settings_general_folders_choose))
                    }
                )

                if (folderPickerSupportsInitialUri) AssistChip(
                    onClick = {
                        onPickFolderRequest(FileUtil.getUriForPath(dataFolderPath))
                    },
                    label = {
                        Text(stringResource(R.string.settings_general_folders_openAndroidData))
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
    return text.ifEmpty { stringResource(R.string.settings_general_folders_notSet) }
}