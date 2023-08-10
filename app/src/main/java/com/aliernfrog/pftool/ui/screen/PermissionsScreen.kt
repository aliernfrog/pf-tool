package com.aliernfrog.pftool.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.component.FadeVisibility
import com.aliernfrog.pftool.ui.dialog.MapsAccessDialog
import com.aliernfrog.pftool.ui.theme.AppComponentShape
import com.aliernfrog.pftool.util.staticutil.FileUtil
import com.aliernfrog.pftool.util.staticutil.GeneralUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(uriPath: String?, onSuccess: @Composable () -> Unit) {
    val context = LocalContext.current
    var storagePermissions by remember { mutableStateOf(GeneralUtil.checkStoragePermissions(context)) }
    var uriPermissions by remember { mutableStateOf(if (uriPath != null) FileUtil.checkUriPermission(uriPath, context) else true) }
    Crossfade(targetState = (storagePermissions && uriPermissions)) { hasPermissions ->
        if (hasPermissions) onSuccess()
        else AppScaffold(
            title = stringResource(R.string.permissions)
        ) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                PermissionsSetUp(
                    uriPath = uriPath,
                    storagePermissions = storagePermissions,
                    uriPermissions = uriPermissions,
                    onStorageResult =  { storagePermissions = it },
                    onUriResult = { uriPermissions = it }
                )
            }
        }
    }
}

@SuppressLint("InlinedApi")
@Composable
private fun PermissionsSetUp(
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
                onUriResult(FileUtil.checkUriPermission(uriPath, context))
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
}

@Composable
private fun PermissionCard(
    visible: Boolean = true,
    title: String,
    buttons: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    FadeVisibility(
        visible = visible,
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
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
}