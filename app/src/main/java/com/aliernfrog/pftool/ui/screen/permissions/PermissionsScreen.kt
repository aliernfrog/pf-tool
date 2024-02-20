package com.aliernfrog.pftool.ui.screen.permissions

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.enum.FileManagementMethod
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.component.AppTopBar
import com.aliernfrog.pftool.ui.dialog.CustomMessageDialog
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
        var permissionsGranted by remember { mutableStateOf(hasPermissions()) }

        AnimatedContent(permissionsGranted) { showContent ->
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
                            permissionsGranted = hasPermissions()
                        }
                    )
                    FileManagementMethod.SHIZUKU -> ShizukuPermissionsScreen(
                        onUpdateStateRequest = {
                            permissionsGranted = hasPermissions()
                        }
                    )
                }
            }
        }
    }

    if (permissionsViewModel.showSAFWorkaroundDialog) permissionsViewModel.safWorkaroundLevel.let { level ->
        CustomMessageDialog(
            title = level.title?.let { stringResource(it) },
            text = level.description?.let { stringResource(it) },
            confirmButton = level.button,
            onDismissRequest = { permissionsViewModel.showSAFWorkaroundDialog = false }
        )
    }
}