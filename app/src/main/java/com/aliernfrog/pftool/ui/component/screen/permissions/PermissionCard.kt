package com.aliernfrog.pftool.ui.component.screen.permissions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.ui.component.CardWithActions
import com.aliernfrog.pftool.ui.dialog.ChooseFolderIntroDialog

@Composable
fun PermissionCard(
    permissionData: PermissionData,
    onFolderPickRequest: () -> Unit
) {
    var introDialogShown by remember { mutableStateOf(false) }
    if (introDialogShown) ChooseFolderIntroDialog(
        permissionData = permissionData,
        onDismissRequest = { introDialogShown = false },
        onConfirm = {
            onFolderPickRequest()
            introDialogShown = false
        }
    )

    CardWithActions(
        title = stringResource(permissionData.titleId),
        painter = rememberVectorPainter(Icons.Outlined.Warning),
        buttons = {
            Button(
                onClick = {
                    if (permissionData.recommendedPath != null && permissionData.recommendedPathDescriptionId != null)
                        introDialogShown = true
                    else onFolderPickRequest()
                }
            ) {
                Text(stringResource(R.string.permissions_chooseFolder))
            }
        },
        content = permissionData.content
    )
}