package io.github.aliernfrog.pftool_shared.ui.screen.permissions

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.pftool_shared.data.PermissionData
import io.github.aliernfrog.pftool_shared.data.requiresAndroidData
import io.github.aliernfrog.pftool_shared.enum.StorageAccessType
import io.github.aliernfrog.pftool_shared.ui.dialog.ChooseFolderIntroDialog
import io.github.aliernfrog.pftool_shared.ui.dialog.UnrecommendedFolderDialog
import io.github.aliernfrog.pftool_shared.ui.viewmodel.PermissionsViewModel
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.pftool_shared.util.extension.takePersistablePermissions
import io.github.aliernfrog.pftool_shared.util.extension.toPath
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.ui.component.CardWithActions
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.ui.component.verticalSegmentedShape
import io.github.aliernfrog.shared.util.sharedStringResource
import org.koin.androidx.compose.koinViewModel

@Composable
fun SAFPermissionsScreen(
    vararg permissionsData: PermissionData,
    vm: PermissionsViewModel = koinViewModel(),
    onUpdateStateRequest: () -> Unit
) {
    val context = LocalContext.current
    val requiresAndroidData = permissionsData.any { it.requiresAndroidData }
    val needsToDowngradeFiles = requiresAndroidData && PFToolSharedUtil.documentsUIRestrictsAndroidData(context)
            && !vm.ignoreDocumentsUIRestrictions

    AnimatedContent(needsToDowngradeFiles) {
        if (it) DowngradeFiles()
        else SAFPermissionsList(
            *permissionsData, onUpdateStateRequest = onUpdateStateRequest
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DowngradeFiles(
    vm: PermissionsViewModel = koinViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        CardWithActions(
            modifier = Modifier.padding(
                vertical = 8.dp,
                horizontal = 12.dp
            ),
            title = sharedStringResource(PFToolSharedString.PermissionsDowngradeFilesApp),
            buttons = {
                Column {
                    Button(
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { vm.showFilesDowngradeDialog = true }
                    ) {
                        Text(sharedStringResource(PFToolSharedString.PermissionsDowngradeFilesAppUninstall))
                    }
                    OutlinedButton(
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            vm.showShizukuIntroDialog = true
                            vm.storageAccessType = StorageAccessType.SHIZUKU
                        }
                    ) {
                        Text(sharedStringResource(PFToolSharedString.PermissionsDowngradeFilesAppCant))
                    }
                }
            }
        ) {
            Text(sharedStringResource(PFToolSharedString.PermissionsDowngradeFilesAppDescription))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SAFPermissionsList(
    vararg permissionsData: PermissionData,
    vm: PermissionsViewModel = koinViewModel(),
    onUpdateStateRequest: () -> Unit
) {
    val context = LocalContext.current
    var missingPermissions by remember { mutableStateOf(
        vm.getMissingUriPermissions(*permissionsData, context = context)
    ) }

    fun onUpdateState() {
        missingPermissions = vm.getMissingUriPermissions(*permissionsData, context = context)
        onUpdateStateRequest()
    }

    var activePermissionData by remember { mutableStateOf<PermissionData?>(null) }
    var unrecommendedPathWarningUri by remember { mutableStateOf<Uri?>(null) }

    fun takePersistableUriPermissions(uri: Uri) {
        uri.takePersistablePermissions(context)
        activePermissionData?.pref?.value = uri.toString()
        onUpdateState()
    }

    val uriPermsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree(), onResult = {
        if (it == null) return@rememberLauncherForActivityResult if (activePermissionData?.requiresAndroidData != true) {}
        else unrecommendedPathWarningUri = Uri.EMPTY

        if (activePermissionData?.forceRecommendedPath == true) {
            val recommendedPath = activePermissionData?.recommendedPath
            val resolvedPath = it.toPath()
            val isRecommendedPath = resolvedPath.equals(recommendedPath, ignoreCase = true)
            if (!isRecommendedPath) unrecommendedPathWarningUri = it
            else takePersistableUriPermissions(it)
        } else takePersistableUriPermissions(it)
    })

    fun openFolderPicker(permissionData: PermissionData) {
        val starterUri = permissionData.recommendedPath?.let {
            PFToolSharedUtil.getUriForPath(it)
        }
        uriPermsLauncher.launch(starterUri)
        activePermissionData = permissionData
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            PermissionsScreenAction(
                title = null,
                description = sharedStringResource(PFToolSharedString.PermissionsSAFFoldersNeeded),
                icon = Icons.Default.Folder,
                button = null
            )
        }

        itemsIndexed(missingPermissions) { index, permissionData ->
            var introDialogShown by remember { mutableStateOf(false) }
            if (introDialogShown) ChooseFolderIntroDialog(
                permissionData = permissionData,
                onDismissRequest = { introDialogShown = false },
                onConfirm = {
                    openFolderPicker(permissionData)
                    introDialogShown = false
                }
            )

            fun onClick() {
                if (permissionData.recommendedPath != null && permissionData.recommendedPathDescription != null)
                    introDialogShown = true
                else openFolderPicker(permissionData)
            }

            ListItem(
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                headlineContent = { Text(stringResource(permissionData.title)) },
                supportingContent = {
                    Column(Modifier.fillMaxWidth()) {
                        permissionData.content()

                        permissionData.recommendedPathWarning?.let { warning ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = stringResource(warning),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Button(
                            onClick = ::onClick,
                            shapes = ButtonDefaults.shapes(),
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 4.dp)
                        ) {
                            Text(sharedStringResource(PFToolSharedString.PermissionsChooseFolder))
                        }
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .verticalSegmentedShape(index = index, totalSize = missingPermissions.size)
                    .clickable(onClick = ::onClick)
            )
        }

        if (StorageAccessType.ALL_FILES.isCompatible()) item {
            ExpressiveSection(
                title = sharedStringResource(PFToolSharedString.PermissionsOther)
            ) {
                ExpressiveButtonRow(
                    title = sharedStringResource(PFToolSharedString.PermissionsSAFAllFiles),
                    description = sharedStringResource(PFToolSharedString.PermissionsSAFAllFilesDescription),
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .verticalSegmentedShape()
                ) {
                    vm.storageAccessType = StorageAccessType.ALL_FILES
                }
            }
        }

        item {
            Spacer(Modifier.navigationBarsPadding())
        }
    }

    unrecommendedPathWarningUri?.let { uri ->
        UnrecommendedFolderDialog(
            permissionData = activePermissionData!!,
            chosenUri = uri,
            onDismissRequest = { unrecommendedPathWarningUri = null },
            onUseUnrecommendedFolderRequest = {
                takePersistableUriPermissions(uri)
                unrecommendedPathWarningUri = null
            },
            onChooseFolderRequest = {
                activePermissionData?.let { openFolderPicker(it) }
                unrecommendedPathWarningUri = null
            }
        )
    }
}