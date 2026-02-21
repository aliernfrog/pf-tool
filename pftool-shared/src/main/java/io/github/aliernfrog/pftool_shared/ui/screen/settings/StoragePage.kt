package io.github.aliernfrog.pftool_shared.ui.screen.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import io.github.aliernfrog.pftool_shared.enum.StorageAccessType
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.pftool_shared.util.extension.takePersistablePermissions
import io.github.aliernfrog.pftool_shared.util.extension.toPath
import io.github.aliernfrog.pftool_shared.util.externalStorageRoot
import io.github.aliernfrog.pftool_shared.util.folderPickerSupportsInitialUri
import io.github.aliernfrog.pftool_shared.util.sharedStringResource
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.FadeVisibility
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowHeader
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.ui.component.form.ExpandableRow
import io.github.aliernfrog.shared.ui.screen.settings.SettingsPageContainer
import io.github.aliernfrog.shared.util.extension.horizontalFadingEdge
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager

@Composable
fun StoragePage(
    storageAccessTypePref: BasePreferenceManager.Preference<Int>,
    folderPrefs: Map<String, BasePreferenceManager.Preference<String>>,
    onEnableStorageAccessTypeRequest: (StorageAccessType) -> Unit,
    onNavigateBackRequest: () -> Unit
) {
    var storageAccessTypesExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val selectedStorageAccessType = StorageAccessType.entries[storageAccessTypePref.value]

    SettingsPageContainer(
        title = sharedStringResource(PFToolSharedString::settingsStorage),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        VerticalSegmentor(
            {
                ExpandableRow(
                    expanded = storageAccessTypesExpanded,
                    title = sharedStringResource(PFToolSharedString::settingsStorageStorageAccessType),
                    description = sharedStringResource(selectedStorageAccessType.label),
                    onClickHeader = { storageAccessTypesExpanded = !storageAccessTypesExpanded }
                ) {
                    val choices: List<@Composable () -> Unit> = StorageAccessType.entries.filter { it.isCompatible() }.map { type -> {
                        val selected = storageAccessTypePref.value == type.ordinal

                        fun onSelect() {
                            if (!selected) onEnableStorageAccessTypeRequest(type)
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
                                    title = sharedStringResource(type.label),
                                    description = sharedStringResource(type.description)
                                )
                            }
                        }
                    }
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

        ExpressiveSection(
            title = sharedStringResource(PFToolSharedString::settingsStorageFolders)
        ) {
            FolderConfiguration(
                useRawPathInputs = selectedStorageAccessType != StorageAccessType.SAF,
                prefs = folderPrefs
            )
        }
    }
}

@Composable
private fun FolderConfiguration(
    prefs: Map<String, BasePreferenceManager.Preference<String>>,
    useRawPathInputs: Boolean
) {
    val context = LocalContext.current
    var activePref: BasePreferenceManager.Preference<String>? = remember { null }

    val openFolderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            val pref = activePref ?: return@rememberLauncherForActivityResult
            if (!useRawPathInputs) uri.takePersistablePermissions(context)
            pref.value = uri.toString().let {
                if (useRawPathInputs) PFToolSharedUtil.getFilePath(it) else it
            }
        }
    )

    AnimatedContent(useRawPathInputs) { rawPathInput ->
        val items: List<@Composable () -> Unit> = prefs.map { (label, pref) -> {

            if (rawPathInput) RawPathItem(
                label = label,
                pref = pref,
                onPickFolderRequest = {
                    activePref = pref
                    openFolderLauncher.launch(null)
                }
            )
            else FolderConfigItem(
                label = label,
                pref = pref,
                path = getFolderDescription(pref.value),
                onPickFolderRequest = { uri ->
                    activePref = pref
                    openFolderLauncher.launch(uri)
                }
            )
        } }

        VerticalSegmentor(
            *items.toTypedArray(),
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RawPathItem(
    label: String,
    pref: BasePreferenceManager.Preference<String>,
    onPickFolderRequest: () -> Unit
) {
    val currentPath = pref.value
    val isDefault = pref.defaultValue == currentPath
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = currentPath,
            onValueChange = {
                pref.value = it
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        FadeVisibility(!isDefault) {
            SuggestionCard(
                title = sharedStringResource(PFToolSharedString::settingsStorageFoldersRestoreDefault),
                description = pref.defaultValue
            ) {
                pref.value = pref.defaultValue
            }
        }

        FilledTonalButton(
            onClick = onPickFolderRequest,
            shapes = ButtonDefaults.shapes(),
            modifier = Modifier.align(Alignment.End)
        ) {
            ButtonIcon(rememberVectorPainter(Icons.Default.FolderOpen))
            Text(sharedStringResource(PFToolSharedString::settingsStorageFoldersChoose))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FolderConfigItem(
    label: String,
    pref: BasePreferenceManager.Preference<String>,
    path: String,
    onPickFolderRequest: (Uri?) -> Unit
) {
    val buttonsScrollState = rememberScrollState()
    val recommendedPath = remember { pref.defaultValue.removePrefix(externalStorageRoot) }
    val dataFolderPath = remember { externalStorageRoot+"Android/data" }
    val usingRecommendedPath = path.equals(recommendedPath, ignoreCase = true)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = path,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 15.sp
        )

        if (!usingRecommendedPath) SuggestionCard(
            title = sharedStringResource(
                if (folderPickerSupportsInitialUri) PFToolSharedString::settingsStorageFoldersOpenRecommended
                else PFToolSharedString::settingsStorageFoldersRecommendedFolder
            ),
            description = recommendedPath,
            enabled = folderPickerSupportsInitialUri
        ) {
            onPickFolderRequest(PFToolSharedUtil.getUriForPath(pref.defaultValue))
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
            AssistChip(
                onClick = { onPickFolderRequest(null) },
                label = {
                    Text(sharedStringResource(PFToolSharedString::settingsStorageFoldersChoose))
                }
            )

            AssistChip(
                onClick = {
                    onPickFolderRequest(PFToolSharedUtil.getUriForPath(path))
                },
                label = {
                    Text(sharedStringResource(PFToolSharedString::settingsStorageFoldersOpenCurrent))
                }
            )

            if (folderPickerSupportsInitialUri) AssistChip(
                onClick = {
                    onPickFolderRequest(PFToolSharedUtil.getUriForPath(dataFolderPath))
                },
                label = {
                    Text(sharedStringResource(PFToolSharedString::settingsStorageFoldersOpenAndroidData))
                }
            )
        }
    }
}

@Composable
private fun SuggestionCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedCard(
        enabled = enabled,
        onClick = onClick,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(
            vertical = 4.dp,
            horizontal = 16.dp
        )) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun getFolderDescription(path: String): String {
    var text = path
    if (text.isNotEmpty()) try {
        text = text.toUri().toPath().removePrefix(externalStorageRoot)
    } catch (_: Exception) {}
    return text.ifEmpty {
        sharedStringResource(PFToolSharedString::settingsStorageFoldersNotSet)
    }
}