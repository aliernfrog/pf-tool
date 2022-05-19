package com.aliernfrog.pftool.ui.sheets

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolRoundedModalBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val allFilesAccess = Build.VERSION.SDK_INT >= 30

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PermissionSheet() {
    val context = LocalContext.current
    val state = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded)
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
fun OkButton(scope: CoroutineScope, state: ModalBottomSheetState) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (it) scope.launch { state.hide() }
        })
    Button(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        ),
        contentPadding = PaddingValues(all = 16.dp),
        content = { Text(context.getString(R.string.action_ok), fontWeight = FontWeight.Bold) },
        onClick = {
            if (allFilesAccess) {
                if (Environment.isExternalStorageManager()) {
                    scope.launch { state.hide() }
                } else {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    scope.launch { state.hide() }
                } else {
                    launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
    )
}