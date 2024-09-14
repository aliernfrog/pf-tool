package com.aliernfrog.pftool.ui.screen.permissions

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.enum.StorageAccessType
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.component.AppTopBar
import com.aliernfrog.pftool.ui.component.SettingsButton
import com.aliernfrog.pftool.ui.dialog.CustomMessageDialog
import com.aliernfrog.pftool.ui.viewmodel.PermissionsViewModel
import com.aliernfrog.pftool.ui.viewmodel.ShizukuViewModel
import com.aliernfrog.pftool.util.staticutil.GeneralUtil
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    vararg permissionsData: PermissionData,
    title: String,
    permissionsViewModel: PermissionsViewModel = koinViewModel(),
    shizukuViewModel: ShizukuViewModel = koinViewModel(),
    onNavigateSettingsRequest: () -> Unit,
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

    var permissionsGranted by remember { mutableStateOf(hasPermissions(), neverEqualPolicy()) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        permissionsGranted = hasPermissions()
    }

    AnimatedContent(
        StorageAccessType.entries[permissionsViewModel.prefs.storageAccessType.value]
    ) { method ->
        AnimatedContent(permissionsGranted) { showContent ->
            if (showContent) content()
            else AppScaffold(
                topBar = { AppTopBar(
                    title = title,
                    scrollBehavior = it,
                    actions = {
                        SettingsButton(onClick = onNavigateSettingsRequest)
                    }
                ) }
            ) {
                when (method) {
                    StorageAccessType.SAF -> SAFPermissionsScreen(
                        *permissionsData,
                        onUpdateStateRequest = {
                            permissionsGranted = hasPermissions()
                        }
                    )
                    StorageAccessType.SHIZUKU -> ShizukuPermissionsScreen(
                        onUpdateStateRequest = {
                            permissionsGranted = hasPermissions()
                        }
                    )
                    StorageAccessType.ALL_FILES -> AllFilesPermissionsScreen(
                        onUpdateStateRequest = {
                            permissionsGranted = hasPermissions()
                        }
                    )
                }
            }
        }
    }

    if (permissionsViewModel.showShizukuIntroDialog) CustomMessageDialog(
        title = stringResource(R.string.permissions_setupShizuku),
        text = stringResource(R.string.permissions_setupShizuku_description),
        onDismissRequest = { permissionsViewModel.showShizukuIntroDialog = false }
    )

    if (permissionsViewModel.showFilesDowngradeDialog) CustomMessageDialog(
        title = null,
        text = stringResource(R.string.permissions_downgradeFilesApp_guide)
            .replace("{CANT_UNINSTALL_TEXT}", stringResource(R.string.permissions_downgradeFilesApp_cant)),
        onDismissRequest = { permissionsViewModel.showFilesDowngradeDialog = false },
        confirmButton = {
            Button(
                onClick = {
                    val documentsUIPackage = GeneralUtil.getDocumentsUIPackage(context) ?: return@Button
                    permissionsViewModel.showFilesDowngradeDialog = false
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.parse("package:${documentsUIPackage.packageName}")
                        context.startActivity(this)
                    }
                }
            ) {
                Text(stringResource(R.string.action_ok))
            }
        }
    )
}