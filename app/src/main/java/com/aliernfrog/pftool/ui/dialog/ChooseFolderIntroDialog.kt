package com.aliernfrog.pftool.ui.dialog

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.externalStorageRoot
import com.aliernfrog.pftool.ui.component.form.DividerRow

@Composable
fun ChooseFolderIntroDialog(
    permissionData: PermissionData,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text(stringResource(R.string.action_ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        title = {
            Text(stringResource(R.string.permissions_recommendedFolder))
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(permissionData.recommendedPathDescriptionId!!),
                )
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = permissionData.recommendedPath!!.removePrefix(externalStorageRoot),
                        //fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(4.dp)
                    )
                }

                DividerRow(Modifier.padding(vertical = 2.dp))

                Text(
                    text = stringResource(
                        //? Folder picker on Android 7 or below doesn't support automatically navigating
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) R.string.permissions_recommendedFolder_a8Hint
                        else R.string.permissions_recommendedFolder_a7Hint
                    ),
                    fontWeight = FontWeight.W300
                )

                permissionData.doesntExistHintId?.let { Text(
                    text = stringResource(it),
                    fontWeight = FontWeight.W300
                ) }
            }
        }
    )
}