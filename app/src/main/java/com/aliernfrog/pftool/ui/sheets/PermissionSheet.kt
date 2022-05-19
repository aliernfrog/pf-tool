package com.aliernfrog.pftool.ui.sheets

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolRoundedModalBottomSheet

@Composable
fun PermissionSheet() {
    val context = LocalContext.current
    PFToolRoundedModalBottomSheet(title = context.getString(R.string.warning_missingPermissions)) {
        PFToolColumnRounded {
            Text(context.getString(R.string.info_storagePermission), fontWeight = FontWeight.Bold)
        }
        PFToolButton(title = context.getString(R.string.action_ok), backgroundColor = MaterialTheme.colors.primary) {

        }
    }
}