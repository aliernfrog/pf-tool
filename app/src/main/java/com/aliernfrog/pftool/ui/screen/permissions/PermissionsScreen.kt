package com.aliernfrog.pftool.ui.screen.permissions

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.component.SettingsButton
import com.aliernfrog.pftool.ui.viewmodel.PermissionsViewModel
import io.github.aliernfrog.pftool_shared.data.PermissionData
import io.github.aliernfrog.pftool_shared.enum.StorageAccessType
import io.github.aliernfrog.pftool_shared.ui.dialog.CustomMessageDialog
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppTopBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PermissionsScreen(
    vararg permissionsData: PermissionData,
    title: String,
    vm: PermissionsViewModel = koinViewModel(),
    onNavigateSettingsRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val isShizukuFileServiceRunning = vm.isShizukuFileServiceRunning.collectAsState().value

    fun hasPermissions(): Boolean {
        return vm.hasPermissions(
            *permissionsData,
            isShizukuFileServiceRunning = isShizukuFileServiceRunning,
            context = context
        )
    }

    var permissionsGranted by remember { mutableStateOf(hasPermissions(), neverEqualPolicy()) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        permissionsGranted = hasPermissions()
    }

    AnimatedContent(
        StorageAccessType.entries[vm.prefs.storageAccessType.value]
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

    if (vm.showShizukuIntroDialog) CustomMessageDialog(
        title = stringResource(R.string.permissions_setupShizuku),
        text = stringResource(R.string.permissions_setupShizuku_description),
        onDismissRequest = { vm.showShizukuIntroDialog = false }
    )

    if (vm.showFilesDowngradeDialog) CustomMessageDialog(
        title = null,
        text = stringResource(R.string.permissions_downgradeFilesApp_guide)
            .replace("{CANT_UNINSTALL_TEXT}", stringResource(R.string.permissions_downgradeFilesApp_cant)),
        onDismissRequest = { vm.showFilesDowngradeDialog = false },
        confirmButton = {
            Button(
                shapes = ButtonDefaults.shapes(),
                onClick = {
                    val documentsUIPackage = PFToolSharedUtil.getDocumentsUIPackage(context) ?: return@Button
                    vm.showFilesDowngradeDialog = false
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = "package:${documentsUIPackage.packageName}".toUri()
                        context.startActivity(this)
                    }
                }
            ) {
                Text(stringResource(R.string.action_ok))
            }
        }
    )
}