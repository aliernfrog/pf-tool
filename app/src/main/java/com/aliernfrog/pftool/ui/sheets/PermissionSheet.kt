package com.aliernfrog.pftool.ui.sheets

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolRoundedModalBottomSheet
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
        PFToolButton(title = context.getString(R.string.action_ok), contentColor = MaterialTheme.colors.onPrimary, backgroundColor = MaterialTheme.colors.primary) {
            scope.launch { state.hide() }
        }
    }
}