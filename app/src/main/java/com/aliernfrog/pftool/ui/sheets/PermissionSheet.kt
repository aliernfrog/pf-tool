package com.aliernfrog.pftool.ui.sheets

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolRoundedModalBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PermissionSheet() {
    val context = LocalContext.current
    val state = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded)
    val scope = rememberCoroutineScope()
    PFToolRoundedModalBottomSheet(title = context.getString(R.string.warning_missingPermissions), state) {
        PFToolColumnRounded {
            Text(context.getString(R.string.info_storagePermission), fontWeight = FontWeight.Bold)
        }
        OkButton(scope, state)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OkButton(scope: CoroutineScope, state: ModalBottomSheetState) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = {
        if (it) scope.launch { state.hide() }
    })
    PFToolButton(title = context.getString(R.string.action_ok), contentColor = MaterialTheme.colors.onPrimary, backgroundColor = MaterialTheme.colors.primary) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            scope.launch { state.hide() }
        } else {
            launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
}