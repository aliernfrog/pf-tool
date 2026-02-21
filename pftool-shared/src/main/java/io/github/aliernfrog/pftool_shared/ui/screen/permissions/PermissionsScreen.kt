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
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.aliernfrog.pftool_shared.data.PermissionData
import io.github.aliernfrog.pftool_shared.enum.StorageAccessType
import io.github.aliernfrog.pftool_shared.ui.dialog.CustomMessageDialog
import io.github.aliernfrog.pftool_shared.ui.viewmodel.IPermissionsViewModel
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.pftool_shared.util.sharedStringResource
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppTopBar
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.util.SharedString
import org.koin.androidx.compose.koinViewModel
import io.github.aliernfrog.shared.util.sharedStringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PermissionsScreen(
    vararg permissionsData: PermissionData,
    title: String,
    vm: IPermissionsViewModel = koinViewModel(),
    onRestartAppRequest: () -> Unit,
    onNavigateStorageSettingsRequest: () -> Unit,
    settingsButton: (@Composable () -> Unit)?,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val isShizukuFileServiceRunning = vm.isShizukuFileServiceRunning.collectAsStateWithLifecycle().value

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
                        vm = vm,
                        onUpdateStateRequest = {
                            permissionsGranted = hasPermissions()
                        }
                    )
                    StorageAccessType.SHIZUKU -> ShizukuPermissionsScreen(
                        vm = vm,
                        onRestartAppRequest = onRestartAppRequest,
                        onUpdateStateRequest = {
                            permissionsGranted = hasPermissions()
                        },
                        onNavigateStorageSettingsRequest = onNavigateStorageSettingsRequest
                    )
                    StorageAccessType.ALL_FILES -> AllFilesPermissionsScreen(
                        vm = vm,
                        onUpdateStateRequest = {
                            permissionsGranted = hasPermissions()
                        }
                    )
                }
            }
        }
    }

    if (vm.showShizukuIntroDialog) CustomMessageDialog(
        title = sharedStringResource(PFToolSharedString::permissionsSetupShizuku),
        text = sharedStringResource(PFToolSharedString::permissionsSetupShizukuDescription),
        onDismissRequest = { vm.showShizukuIntroDialog = false }
    )

    if (vm.showFilesDowngradeDialog) CustomMessageDialog(
        title = null,
        text = sharedStringResource(PFToolSharedString::permissionsDowngradeFilesAppGuide)
            .replace(
                "{CANT_UNINSTALL_TEXT}", sharedStringResource(PFToolSharedString::permissionsDowngradeFilesAppCant)
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
                Text(sharedStringResource(SharedString::actionOK))
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth().padding(12.dp)
    ) {
        icon?.let {
            ExpressiveRowIcon(
                painter = rememberVectorPainter(it),
                iconSize = 80.dp,
                shape = MaterialShapes.PixelCircle.toShape()
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
                style = MaterialTheme.typography.bodyLargeEmphasized,
                textAlign = TextAlign.Center
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