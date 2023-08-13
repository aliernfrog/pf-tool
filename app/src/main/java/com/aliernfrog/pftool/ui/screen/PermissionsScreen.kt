package com.aliernfrog.pftool.ui.screen

import android.content.Intent
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.theme.AppComponentShape
import com.aliernfrog.pftool.util.staticutil.FileUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    vararg permissionsData: PermissionData,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    fun getMissingPermissions(): List<PermissionData> {
        return permissionsData.filter {
            !FileUtil.hasUriPermission(it.uri, context)
        }
    }

    var missingPermissions by remember { mutableStateOf(
        getMissingPermissions()
    ) }

    Crossfade(targetState = missingPermissions.isEmpty()) { hasPermissions ->
        if (hasPermissions) content()
        else AppScaffold(
            title = stringResource(R.string.permissions)
        ) {
            PermissionsList(
                missingPermissions = missingPermissions,
                onUpdateState = {
                    missingPermissions = getMissingPermissions()
                }
            )
        }
    }
}

@Composable
private fun PermissionsList(
    missingPermissions: List<PermissionData>,
    onUpdateState: () -> Unit
) {
    val context = LocalContext.current
    val uriPermsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree(), onResult = {
        if (it != null) {
            // TODO ensure correct folder picked, show dialog if not
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.grantUriPermission(context.packageName, it, takeFlags)
            context.contentResolver.takePersistableUriPermission(it, takeFlags)
            onUpdateState()
        }
    })

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(missingPermissions) { cardData ->
            fun requestUriPermission() {
                // TODO? always use uri
                val treeId = "primary:"+cardData.uri.removePrefix("${Environment.getExternalStorageDirectory()}/")
                val uri = DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", treeId)
                uriPermsLauncher.launch(uri)
            }

            var introDialogShown by remember { mutableStateOf(false) }
            cardData.introDialog?.let { it(
                shown = introDialogShown,
                onDismissRequest = {
                    introDialogShown = false
                },
                onConfirm = {
                    requestUriPermission()
                    introDialogShown = false
                }
            ) }

            PermissionCard(
                title = stringResource(cardData.titleId),
                buttons = {
                    Button(
                        onClick = {
                            if (cardData.introDialog != null) introDialogShown = true
                            else requestUriPermission()
                        }
                    ) {
                        Text(stringResource(R.string.permissions_chooseFolder))
                    }
                },
                content = cardData.content
            )
        }
    }
}

/*@SuppressLint("InlinedApi")
@Composable
private fun PermissiodnsSetUp(
    uriPath: String?,
    storagePermissions: Boolean,
    uriPermissions: Boolean,
    onStorageResult: (Boolean) -> Unit,
    onUriResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val allFilesAccess = Build.VERSION.SDK_INT >= 30

    val storagePermsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = {
        onStorageResult(GeneralUtil.checkStoragePermissions(context))
    })
    val allFilesPermsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(), onResult = {
        onStorageResult(GeneralUtil.checkStoragePermissions(context))
    })
    PermissionCard(
        visible = !storagePermissions,
        title = stringResource(R.string.permissions_storage),
        buttons = {
            Button(
                onClick = {
                    if (allFilesAccess) {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.data = Uri.fromParts("package", context.packageName, null)
                        allFilesPermsLauncher.launch(intent)
                    } else storagePermsLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            ) {
                Text(stringResource(R.string.permissions_allowAccess))
            }
        }
    ) {
        Text(stringResource(R.string.permissions_storage_description))
    }

    if (uriPath != null) {
        var uriDialogShown by rememberSaveable {
            mutableStateOf(false)
        }
        val uriPermsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree(), onResult = {
            if (it != null) {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.grantUriPermission(context.packageName, it, takeFlags)
                context.contentResolver.takePersistableUriPermission(it, takeFlags)
                onUriResult(FileUtil.hasUriPermission(uriPath, context))
            }
        })

        PermissionCard(
            visible = !uriPermissions,
            title = stringResource(R.string.permissions_maps),
            buttons = {
                Button(
                    onClick = {
                        uriDialogShown = true
                    }
                ) {
                    Text(stringResource(R.string.permissions_allowAccess))
                }
            }
        ) {
            Text(stringResource(R.string.permissions_maps_description))
        }

        if (uriDialogShown) MapsAccessDialog(
            mapsPath = uriPath,
            onDismissRequest = { uriDialogShown = false },
            onConfirm = {
                val treeId = uriPath.replace("${Environment.getExternalStorageDirectory()}/", "primary:")
                val uri = DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", treeId)
                uriPermsLauncher.launch(uri)
                uriDialogShown = false
            }
        )
    }
}*/

@Composable
private fun PermissionCard(
    title: String,
    buttons: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        shape = AppComponentShape
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            content()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                buttons()
            }
        }
    }
}