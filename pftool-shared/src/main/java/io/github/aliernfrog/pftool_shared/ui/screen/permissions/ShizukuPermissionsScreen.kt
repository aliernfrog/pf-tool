package io.github.aliernfrog.pftool_shared.ui.screen.permissions

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotStarted
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.aliernfrog.pftool_shared.enum.ShizukuStatus
import io.github.aliernfrog.pftool_shared.impl.ShizukuManager
import io.github.aliernfrog.pftool_shared.ui.viewmodel.IPermissionsViewModel
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.CardWithActions
import io.github.aliernfrog.shared.ui.component.FadeVisibility
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.ui.component.verticalSegmentedShape
import io.github.aliernfrog.shared.ui.theme.AppComponentShape
import io.github.aliernfrog.shared.util.sharedStringResource
import org.koin.androidx.compose.koinViewModel
import rikka.shizuku.Shizuku

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ShizukuPermissionsScreen(
    vm: IPermissionsViewModel = koinViewModel(),
    onRestartAppRequest: () -> Unit,
    onUpdateStateRequest: () -> Unit,
    onNavigateStorageSettingsRequest: () -> Unit
) {
    val context = LocalContext.current

    val shizukuStatus = vm.shizukuManager.status.collectAsStateWithLifecycle().value
    val shizukuInstalled = vm.shizukuManager.shizukuInstalled
    val shizukuFileServiceRunning = vm.isShizukuFileServiceRunning.collectAsStateWithLifecycle().value
    val shizukuTimedOut = vm.shizukuManager.timedOut.collectAsStateWithLifecycle().value
    val shizukuVersionProblematic = vm.shizukuManager.shizukuVersionProblematic.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        vm.shizukuManager.checkAvailability(context)
    }

    LaunchedEffect(shizukuFileServiceRunning) {
        onUpdateStateRequest()
    }

    AnimatedContent(
        shizukuStatus == ShizukuStatus.AVAILABLE
    ) { isLoading ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
            verticalArrangement = if (isLoading) Arrangement.Center else Arrangement.Top
        ) {
            if (isLoading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 12.dp)
                        .clip(AppComponentShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    ContainedLoadingIndicator()
                    Text(
                        text = sharedStringResource(PFToolSharedString.PermissionsShizukuWaitingService),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                AnimatedVisibility(
                    visible = shizukuTimedOut,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (shizukuVersionProblematic) ProblematicManagerCard(
                            shizukuVersion = vm.currentShizukuVersion,
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(16.dp)
                        )

                        CardWithActions(
                            title = null,
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(16.dp),
                            buttons = {
                                if (shizukuInstalled) TextButton(
                                    shapes = ButtonDefaults.shapes(),
                                    onClick = {
                                        vm.shizukuManager.launchShizuku(context)
                                    }
                                ) {
                                    ButtonIcon(rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew))
                                    Text(sharedStringResource(PFToolSharedString.PermissionsShizukuOpenShizuku))
                                }
                                Button(
                                    shapes = ButtonDefaults.shapes(),
                                    onClick = {
                                        vm.shizukuManager.disableShizukuNeverLoadPref()
                                        onRestartAppRequest()
                                    }
                                ) {
                                    ButtonIcon(rememberVectorPainter(Icons.Default.RestartAlt))
                                    Text(sharedStringResource(PFToolSharedString.PermissionsShizukuWaitingServiceTimedOutRestart))
                                }
                            }
                        ) {
                            Text(sharedStringResource(PFToolSharedString.PermissionsShizukuWaitingServiceTimedOut))
                        }
                    }
                }
            } else ShizukuSetupGuide(
                shizukuInstalled = shizukuInstalled,
                shizukuStatus = shizukuStatus,
                deviceRooted = vm.shizukuManager.deviceRooted,
                onLaunchShizukuRequest = {
                    vm.shizukuManager.launchShizuku(context)
                },
                onNavigateStorageSettingsRequest = onNavigateStorageSettingsRequest
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ProblematicManagerCard(
    shizukuVersion: String,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    CardWithActions(
        title = null,
        modifier = modifier,
        buttons = {
            TextButton(
                shapes = ButtonDefaults.shapes(),
                onClick = {
                    uriHandler.openUri(ShizukuManager.SHIZUKU_RELEASES_URL)
                }
            ) {
                ButtonIcon(rememberVectorPainter(Icons.Default.OpenInBrowser))
                Text(sharedStringResource(PFToolSharedString.PermissionsShizukuProblematicVersionAllVersions))
            }

            Button(
                shapes = ButtonDefaults.shapes(),
                onClick = {
                    uriHandler.openUri(ShizukuManager.SHIZUKU_RECOMMENDED_VERSION_DOWNLOAD_URL)
                }
            ) {
                ButtonIcon(rememberVectorPainter(Icons.Default.Download))
                Text(sharedStringResource(PFToolSharedString.PermissionsShizukuProblematicVersionDownloadRecommended))
            }
        }
    ) {
        Text(
            text = sharedStringResource(PFToolSharedString.PermissionsShizukuProblematicVersion)
                .replace("{CURRENT_VERSION}", shizukuVersion)
                .replace("{RECOMMENDED_VERSION}", ShizukuManager.SHIZUKU_RECOMMENDED_VERSION_NAME)
        )
        Text(
            text = sharedStringResource(PFToolSharedString.PermissionsShizukuProblematicVersionNote)
                .replace("{CURRENT_VERSION}", shizukuVersion),
            style = MaterialTheme.typography.bodySmallEmphasized
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ShizukuSetupGuide(
    shizukuInstalled: Boolean,
    shizukuStatus: ShizukuStatus,
    deviceRooted: Boolean,
    onLaunchShizukuRequest: () -> Unit,
    onNavigateStorageSettingsRequest: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    AnimatedContent(shizukuStatus) { status ->
        val title = when (status) {
            ShizukuStatus.UNKNOWN, ShizukuStatus.NOT_INSTALLED -> PFToolSharedString.PermissionsShizukuInstallTitle
            ShizukuStatus.WAITING_FOR_BINDER -> PFToolSharedString.PermissionsShizukuNotRunning
            ShizukuStatus.UNAUTHORIZED -> PFToolSharedString.PermissionsShizukuPermission
            else -> null
        }
        val description = when (status) {
            ShizukuStatus.UNKNOWN, ShizukuStatus.NOT_INSTALLED -> PFToolSharedString.PermissionsShizukuIntroduction
            ShizukuStatus.WAITING_FOR_BINDER -> PFToolSharedString.PermissionsShizukuNotRunningDescription
            ShizukuStatus.UNAUTHORIZED -> PFToolSharedString.PermissionsShizukuPermissionDescription
            else -> null
        }
        val icon = when (status) {
            ShizukuStatus.UNKNOWN, ShizukuStatus.NOT_INSTALLED -> Icons.Default.Download
            ShizukuStatus.WAITING_FOR_BINDER -> Icons.Default.NotStarted
            ShizukuStatus.UNAUTHORIZED -> Icons.Default.Security
            else -> null
        }
        val button: (@Composable () -> Unit)? = { when (status) {
            ShizukuStatus.UNKNOWN, ShizukuStatus.NOT_INSTALLED -> {
                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = onLaunchShizukuRequest
                ) {
                    ButtonIcon(rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew))
                    Text(sharedStringResource(PFToolSharedString.PermissionsShizukuInstallShizuku))
                }
            }
            ShizukuStatus.WAITING_FOR_BINDER -> {
                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = onLaunchShizukuRequest
                ) {
                    ButtonIcon(rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew))
                    Text(sharedStringResource(PFToolSharedString.PermissionsShizukuOpenShizuku))
                }
            }
            ShizukuStatus.UNAUTHORIZED -> {
                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = { Shizuku.requestPermission(0) }
                ) {
                    Text(sharedStringResource(PFToolSharedString.PermissionsShizukuPermissionGrant))
                }
            }
            else -> null
        } }

        PermissionsScreenAction(
            title = title?.let { sharedStringResource(it) },
            description = description?.let { sharedStringResource(it) },
            icon = icon,
            button = button
        )
    }

    FadeVisibility(
        deviceRooted && shizukuStatus != ShizukuStatus.UNAUTHORIZED
    ) {
        CardWithActions(
            title = sharedStringResource(PFToolSharedString.PermissionsShizukuRooted),
            buttons = {
                OutlinedButton(
                    shapes = ButtonDefaults.shapes(),
                    onClick = {
                        uriHandler.openUri(ShizukuManager.SUI_GITHUB)
                    }
                ) {
                    ButtonIcon(rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew))
                    Text(sharedStringResource(PFToolSharedString.PermissionsShizukuSui))
                }
                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = onLaunchShizukuRequest
                ) {
                    ButtonIcon(rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew))
                    Text(sharedStringResource(
                        if (shizukuInstalled) PFToolSharedString.PermissionsShizukuOpenShizuku
                        else PFToolSharedString.PermissionsShizukuInstallShizuku
                    ))
                }
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(
                text = sharedStringResource(PFToolSharedString.PermissionsShizukuRootedDescription),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (shizukuInstalled) ExpressiveButtonRow(
        title = sharedStringResource(PFToolSharedString.Info),
        description = sharedStringResource(PFToolSharedString.PermissionsShizukuIntroduction),
        icon = {
            ExpressiveRowIcon(rememberVectorPainter(Icons.Default.Info))
        },
        modifier = Modifier
            .padding(12.dp)
            .verticalSegmentedShape()
    ) {
        onNavigateStorageSettingsRequest()
    }

    Spacer(Modifier.navigationBarsPadding())
}