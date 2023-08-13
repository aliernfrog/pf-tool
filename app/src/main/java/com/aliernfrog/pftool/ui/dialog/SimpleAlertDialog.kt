package com.aliernfrog.pftool.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R

@Composable
fun SimpleAlertDialog(
    shown: Boolean,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    confirmButtonText: String = stringResource(R.string.action_ok),
    dismissButtonText: String = stringResource(R.string.action_cancel),
    contentSpacing: Dp = 8.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    if (shown) AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(dismissButtonText)
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(contentSpacing),
                content = content
            )
        }
    )
}