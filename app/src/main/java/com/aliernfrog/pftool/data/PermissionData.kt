package com.aliernfrog.pftool.data

import androidx.compose.runtime.Composable

data class PermissionData(
    val titleId: Int,
    val uri: String,
    val introDialog: (@Composable (
        shown: Boolean,
        onDismissRequest: () -> Unit,
        onConfirm: () -> Unit
    ) -> Unit)? = null,
    val content: @Composable () -> Unit
)