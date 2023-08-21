package com.aliernfrog.pftool.data

import android.net.Uri
import androidx.compose.runtime.Composable
import com.aliernfrog.pftool.R

data class PermissionData(
    val titleId: Int,
    val recommendedPath: String?,
    val recommendedPathDescriptionId: Int?,
    val doesntExistHintId: Int? = R.string.permissions_recommendedFolder_manuallyCreate,
    val getUri: () -> String,
    val onUriUpdate: (Uri) -> Unit,
    val content: @Composable () -> Unit
)