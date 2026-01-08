package io.github.aliernfrog.pftool_shared.ui.screen.permissions

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import io.github.aliernfrog.pftool_shared.data.PermissionData
import io.github.aliernfrog.pftool_shared.enum.StorageAccessType
import io.github.aliernfrog.pftool_shared.ui.dialog.CustomMessageDialog
import io.github.aliernfrog.pftool_shared.ui.viewmodel.PermissionsViewModel
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppTopBar
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.sharedStringResource
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PermissionsScreen(
    vararg permissionsData: PermissionData,
    title: String,
    vm: PermissionsViewModel = koinViewModel(),
    onRestartAppRequest: () -> Unit,
    onNavigateStorageSettingsRequest: () -> Unit,
    settingsButton: (@Composable () -> Unit)?,
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

    AnimatedContent(vm.storageAccessType) { method ->
        AnimatedContent(permissionsGranted) { showContent ->
            if (showContent) content()
            else AppScaffold(
                topBar = { AppTopBar(
                    title = title,
                    scrollBehavior = it,
                    actions = {
                        settingsButton?.invoke()
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
                        onRestartAppRequest = onRestartAppRequest,
                        onUpdateStateRequest = {
                            permissionsGranted = hasPermissions()
                        },
                        onNavigateStorageSettingsRequest = onNavigateStorageSettingsRequest
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
        title = sharedStringResource(PFToolSharedString.PermissionsSetupShizuku),
        text = sharedStringResource(PFToolSharedString.PermissionsSetupShizukuDescription),
        onDismissRequest = { vm.showShizukuIntroDialog = false }
    )

    if (vm.showFilesDowngradeDialog) CustomMessageDialog(
        title = null,
        text = sharedStringResource(PFToolSharedString.PermissionsDowngradeFilesAppGuide)
            .replace(
                "{CANT_UNINSTALL_TEXT}",
                sharedStringResource(PFToolSharedString.PermissionsDowngradeFilesAppCant)
            ),
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
                Text(sharedStringResource(SharedString.ActionOK))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PermissionsScreenAction(
    title: String?,
    description: String?,
    icon: ImageVector?,
    button: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth().padding(12.dp)
    ) {
        icon?.let {
            ExpressiveRowIcon(
                painter = rememberVectorPainter(it),
                iconSize = 40.dp
            )
        }

        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleLargeEmphasized,
                textAlign = TextAlign.Center
            )
        }

        description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLargeEmphasized
            )
        }

        button?.let {
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                it()
            }
        }
    }
}