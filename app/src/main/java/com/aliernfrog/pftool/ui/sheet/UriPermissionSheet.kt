package com.aliernfrog.pftool.ui.sheet

import android.content.Intent
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolButtonCentered
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolModalBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UriPermissionSheet(mapsFolder: String, state: ModalBottomSheetState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    PFToolModalBottomSheet(title = context.getString(R.string.warning_missingPermissions), state) {
        PFToolColumnRounded {
            Text(context.getString(R.string.info_mapsFolderPermission), color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold)
            PFToolColumnRounded(color = MaterialTheme.colorScheme.secondary) {
                Text(mapsFolder.replaceFirst(Environment.getExternalStorageDirectory().toString(), context.getString(R.string.internalStorage)), fontFamily = FontFamily.Monospace, fontSize = 14.sp)
            }
        }
        PFToolColumnRounded { Text(context.getString(R.string.info_mapsFolderPermission_note), color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
        OkButton(mapsFolder, scope, state)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OkButton(mapsFolder: String, scope: CoroutineScope, state: ModalBottomSheetState) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree(), onResult = {
        if (it != null) {
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.grantUriPermission(context.packageName, it, takeFlags)
            context.contentResolver.takePersistableUriPermission(it, takeFlags)
            scope.launch { state.hide() }
        }
    })
    PFToolButtonCentered(title = context.getString(R.string.action_ok), containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
        val treeId = mapsFolder.replace("${Environment.getExternalStorageDirectory()}/", "primary:")
        val uri = DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", treeId)
        launcher.launch(uri)
    }
}