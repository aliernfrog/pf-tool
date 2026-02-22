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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.aliernfrog.pftool_shared.enum.ShizukuStatus
import io.github.aliernfrog.pftool_shared.impl.ShizukuManager
import io.github.aliernfrog.pftool_shared.ui.viewmodel.IPermissionsViewModel
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.pftool_shared.util.sharedStringResource
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.CardWithActions
import io.github.aliernfrog.shared.ui.component.FadeVisibility
import io.github.aliernfrog.shared.ui.component.SizedButton
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.ui.component.verticalSegmentedShape
import io.github.aliernfrog.shared.ui.theme.AppComponentShape
import rikka.shizuku.Shizuku
import kotlin.reflect.KProperty1

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ShizukuPermissionsScreen(
    vm: IPermissionsViewModel,
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
                        text = sharedStringResource(PFToolSharedString::permissionsShizukuWaitingService),
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
                                    Text(sharedStringResource(PFToolSharedString::permissionsShizukuOpenShizuku))
                                }
                                Button(
                                    shapes = ButtonDefaults.shapes(),
                                    onClick = {
                                        vm.shizukuManager.disableShizukuNeverLoadPref()
                                        onRestartAppRequest()
                                    }
                                ) {
                                    ButtonIcon(rememberVectorPainter(Icons.Default.RestartAlt))
                                    Text(sharedStringResource(PFToolSharedString::permissionsShizukuWaitingServiceTimedOutRestart))
                                }
                            }
                        ) {
                            Text(sharedStringResource(PFToolSharedString::permissionsShizukuWaitingServiceTimedOut))
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
                Text(sharedStringResource(PFToolSharedString::permissionsShizukuProblematicVersionAllVersions))
            }

            Button(
                shapes = ButtonDefaults.shapes(),
                onClick = {
                    uriHandler.openUri(ShizukuManager.SHIZUKU_RECOMMENDED_VERSION_DOWNLOAD_URL)
                }
            ) {
                ButtonIcon(rememberVectorPainter(Icons.Default.Download))
                Text(sharedStringResource(PFToolSharedString::permissionsShizukuProblematicVersionDownloadRecommended))
            }
        }
    ) {
        Text(
            text = sharedStringResource(PFToolSharedString::permissionsShizukuProblematicVersion)
                .replace("{CURRENT_VERSION}", shizukuVersion)
                .replace("{RECOMMENDED_VERSION}", ShizukuManager.SHIZUKU_RECOMMENDED_VERSION_NAME)
        )
        Text(
            text = sharedStringResource(PFToolSharedString::permissionsShizukuProblematicVersionNote)
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
            ShizukuStatus.UNKNOWN, ShizukuStatus.NOT_INSTALLED -> PFToolSharedString::permissionsShizukuInstallTitle
            ShizukuStatus.WAITING_FOR_BINDER -> PFToolSharedString::permissionsShizukuNotRunning
            ShizukuStatus.UNAUTHORIZED -> PFToolSharedString::permissionsShizukuPermission
            else -> null
        }
        val description = when (status) {
            ShizukuStatus.UNKNOWN, ShizukuStatus.NOT_INSTALLED -> PFToolSharedString::permissionsShizukuIntroduction
            ShizukuStatus.WAITING_FOR_BINDER -> PFToolSharedString::permissionsShizukuNotRunningDescription
            ShizukuStatus.UNAUTHORIZED -> PFToolSharedString::permissionsShizukuPermissionDescription
            else -> null
        }
        val icon = when (status) {
            ShizukuStatus.UNKNOWN, ShizukuStatus.NOT_INSTALLED -> Icons.Default.Download
            ShizukuStatus.WAITING_FOR_BINDER -> Icons.Default.NotStarted
            ShizukuStatus.UNAUTHORIZED -> Icons.Default.Security
            else -> null
        }

        val button: @Composable () -> Unit = when (status) {
            ShizukuStatus.WAITING_FOR_BINDER -> Triple(
                PFToolSharedString::permissionsShizukuOpenShizuku,
                Icons.AutoMirrored.Filled.OpenInNew,
                onLaunchShizukuRequest
            )

            ShizukuStatus.UNAUTHORIZED -> Triple(
                PFToolSharedString::permissionsShizukuPermissionGrant,
                null
            ) { Shizuku.requestPermission(0) }

            // UNKNOWN, NOT_INSTALLED and AVAILABLE
            else -> Triple(
                PFToolSharedString::permissionsShizukuInstallShizuku,
                Icons.AutoMirrored.Filled.OpenInNew,
                onLaunchShizukuRequest
            )
        }.let { (text: KProperty1<PFToolSharedString, Int>, icon: ImageVector?, onClick: () -> Unit) -> {
            SizedButton(
                onClick = onClick,
                size = ButtonDefaults.MediumContainerHeight
            ) { textStyle, iconSpacing, iconSize ->
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = iconSpacing)
                            .size(iconSize)
                    )
                }

                Text(
                    text = sharedStringResource(text),
                    style = textStyle
                )
            }
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
            title = sharedStringResource(PFToolSharedString::permissionsShizukuRooted),
            buttons = {
                OutlinedButton(
                    shapes = ButtonDefaults.shapes(),
                    onClick = {
                        uriHandler.openUri(ShizukuManager.SUI_GITHUB)
                    }
                ) {
                    ButtonIcon(rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew))
                    Text(sharedStringResource(PFToolSharedString::permissionsShizukuSui))
                }
                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = onLaunchShizukuRequest
                ) {
                    ButtonIcon(rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew))
                    Text(sharedStringResource(
                        if (shizukuInstalled) PFToolSharedString::permissionsShizukuOpenShizuku
                        else PFToolSharedString::permissionsShizukuInstallShizuku
                    ))
                }
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(
                text = sharedStringResource(PFToolSharedString::permissionsShizukuRootedDescription),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (shizukuInstalled) ExpressiveButtonRow(
        title = sharedStringResource(PFToolSharedString::info),
        description = sharedStringResource(PFToolSharedString::permissionsShizukuIntroduction),
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