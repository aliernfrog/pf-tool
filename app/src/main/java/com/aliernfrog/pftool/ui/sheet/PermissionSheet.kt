package com.aliernfrog.pftool.ui.sheet

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolButtonCentered
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolRoundedModalBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val allFilesAccess = Build.VERSION.SDK_INT >= 30

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PermissionSheet(state: ModalBottomSheetState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    PFToolRoundedModalBottomSheet(title = context.getString(R.string.warning_missingPermissions), state) {
        PFToolColumnRounded {
            val text = if (allFilesAccess) context.getString(R.string.info_allFilesPermission) else context.getString(R.string.info_storagePermission)
            Text(text, fontWeight = FontWeight.Bold)
        }
        OkButton(scope, state)
    }
}

@SuppressLint("InlinedApi")
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OkButton(scope: CoroutineScope, state: ModalBottomSheetState) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = {})
    PFToolButtonCentered(title = context.getString(R.string.action_ok), backgroundColor = MaterialTheme.colors.primary, contentColor = MaterialTheme.colors.onPrimary) {
        if (allFilesAccess) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        } else launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        scope.launch { state.hide() }
    }
}