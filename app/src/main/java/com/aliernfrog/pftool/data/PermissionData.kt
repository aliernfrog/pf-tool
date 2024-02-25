package com.aliernfrog.pftool.data

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable

data class PermissionData(
    @StringRes val title: Int,
    val recommendedPath: String?,
    @StringRes val recommendedPathDescription: Int?,
    @StringRes val createFolderHint: Int? = null,
    @StringRes val useUnrecommendedAnywayDescription: Int? = null,
    val forceRecommendedPath: Boolean = true,
    val getUri: () -> String,
    val onUriUpdate: (Uri) -> Unit,
    val content: @Composable () -> Unit
)