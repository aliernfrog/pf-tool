package com.aliernfrog.pftool.ui.dialog

import android.os.Build
import android.os.Environment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.theme.AppComponentShape

@Composable
fun MapsAccessDialog(
    mapsPath: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    val simplifiedPath = remember {
        mapsPath.removePrefix(Environment.getExternalStorageDirectory().toString()+"/")
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.permissions_allowAccess))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.action_dismiss))
            }
        },
        title = {
            Text(stringResource(R.string.permissions_maps))
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stringResource(R.string.permissions_maps_help_description))
                Card {
                    Text(
                        text = simplifiedPath,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Text(stringResource(R.string.permissions_maps_help_proceed))
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) WarningCard(
                    text = stringResource(R.string.permissions_maps_help_manualNavigation)
                )
                WarningCard(
                    text = stringResource(R.string.permissions_maps_help_folderDoesntExist)
                )
            }
        }
    )
}

@Composable
private fun WarningCard(
    text: String
) {
    Card(
        shape = AppComponentShape
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 4.dp
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = text
            )
        }
    }
}