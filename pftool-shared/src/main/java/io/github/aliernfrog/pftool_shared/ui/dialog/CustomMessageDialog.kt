package io.github.aliernfrog.pftool_shared.ui.dialog

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.sharedStringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomMessageDialog(
    title: String?,
    text: String?,
    icon: ImageVector? = null,
    confirmButton: (@Composable () -> Unit)? = null,
    dismissButtonText: String = sharedStringResource(SharedString.ActionDismiss),
    onDismissRequest: () -> Unit
) {
    @Composable
    fun DismissButton() {
        TextButton(
            shapes = ButtonDefaults.shapes(),
            onClick = onDismissRequest
        ) {
            Text(dismissButtonText)
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            confirmButton?.invoke() ?: DismissButton()
        },
        dismissButton = if (confirmButton != null) { {
            DismissButton()
        } } else null,
        title = title?.let { {
            Text(title)
        } },
        text = text?.let { {
            Text(
                text = text,
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
        } },
        icon = icon?.let { {
            Icon(
                imageVector = it,
                contentDescription = null
            )
        } }
    )
}