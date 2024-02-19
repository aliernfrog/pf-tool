package com.aliernfrog.pftool.ui.screen.permissions

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.enum.FileManagementMethod
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.component.AppTopBar
import com.aliernfrog.pftool.ui.viewmodel.PermissionsViewModel
import com.aliernfrog.pftool.ui.viewmodel.ShizukuViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    vararg permissionsData: PermissionData,
    title: String,
    permissionsViewModel: PermissionsViewModel = koinViewModel(),
    shizukuViewModel: ShizukuViewModel = koinViewModel(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    fun hasPermissions(): Boolean {
        return permissionsViewModel.hasPermissions(
            *permissionsData,
            isShizukuFileServiceRunning = shizukuViewModel.fileServiceRunning,
            context = context
        )
    }

    AnimatedContent(
        FileManagementMethod.entries[permissionsViewModel.prefs.fileManagementMethod]
    ) { method ->
        var showContent by remember { mutableStateOf(hasPermissions()) }

        if (showContent) content()
        else AppScaffold(
            topBar = { AppTopBar(
                title = title,
                scrollBehavior = it
            ) }
        ) {
            when (method) {
                FileManagementMethod.SAF -> SAFPermissionsScreen(
                    *permissionsData,
                    onUpdateStateRequest = {
                        showContent = hasPermissions()
                    }
                )
                FileManagementMethod.SHIZUKU -> ShizukuPermissionsScreen(
                    onUpdateStateRequest = {
                        showContent = hasPermissions()
                    }
                )
            }
        }
    }
}