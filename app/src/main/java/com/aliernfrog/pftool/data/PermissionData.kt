package com.aliernfrog.pftool.data

import android.net.Uri
import androidx.compose.runtime.Composable

data class PermissionData(
    val titleId: Int,
    val recommendedPath: String?,
    val getUri: () -> String,
    val onUriUpdate: (Uri) -> Unit,
    val introDialog: (@Composable (
        shown: Boolean,
        onDismissRequest: () -> Unit,
        onConfirm: () -> Unit
    ) -> Unit)? = null,
    val content: @Composable () -> Unit
)