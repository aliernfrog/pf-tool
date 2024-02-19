package com.aliernfrog.pftool.ui.screen.permissions

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.enum.FileManagementMethod
import com.aliernfrog.pftool.enum.ShizukuStatus
import com.aliernfrog.pftool.ui.component.ButtonIcon
import com.aliernfrog.pftool.ui.component.CardWithActions
import com.aliernfrog.pftool.ui.theme.AppComponentShape
import com.aliernfrog.pftool.ui.viewmodel.PermissionsViewModel
import com.aliernfrog.pftool.ui.viewmodel.ShizukuViewModel
import org.koin.androidx.compose.koinViewModel
import rikka.shizuku.Shizuku

@Composable
fun ShizukuPermissionsScreen(
    permissionsViewModel: PermissionsViewModel = koinViewModel(),
    shizukuViewModel: ShizukuViewModel = koinViewModel(),
    onUpdateStateRequest: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        shizukuViewModel.checkAvailability(context)
    }

    LaunchedEffect(shizukuViewModel.fileServiceRunning) {
        onUpdateStateRequest()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (shizukuViewModel.installed) Text(
            text = stringResource(R.string.permissions_shizuku_introduction),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )

        AnimatedContent(shizukuViewModel.status) { status ->
            val title = when (status) {
                ShizukuStatus.UNKNOWN, ShizukuStatus.NOT_INSTALLED -> R.string.permissions_shizuku_install_title
                ShizukuStatus.WAITING_FOR_BINDER -> R.string.permissions_shizuku_notRunning
                ShizukuStatus.UNAUTHORIZED -> R.string.permissions_shizuku_permission
                else -> null
            }
            val description = when (status) {
                ShizukuStatus.UNKNOWN, ShizukuStatus.NOT_INSTALLED -> R.string.permissions_shizuku_introduction
                ShizukuStatus.WAITING_FOR_BINDER -> R.string.permissions_shizuku_notRunning_description
                ShizukuStatus.UNAUTHORIZED -> R.string.permissions_shizuku_permission_description
                else -> null
            }
            val button: @Composable () -> Unit = { when (status) {
                ShizukuStatus.UNKNOWN, ShizukuStatus.NOT_INSTALLED -> {
                    Button(
                        onClick = {
                            uriHandler.openUri("https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api")
                        }
                    ) {
                        ButtonIcon(rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew))
                        Text(stringResource(R.string.permissions_shizuku_install_install))
                    }
                }
                ShizukuStatus.WAITING_FOR_BINDER -> {
                    Button(
                        onClick = {
                            context.packageManager.getLaunchIntentForPackage(ShizukuViewModel.SHIZUKU_PACKAGE)?.let {
                                context.startActivity(it)
                            }
                        }
                    ) {
                        ButtonIcon(rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew))
                        Text(stringResource(R.string.permissions_shizuku_notRunning_openShizuku))
                    }
                }
                ShizukuStatus.UNAUTHORIZED -> {
                    Button(
                        onClick = { Shizuku.requestPermission(0) }
                    ) {
                        Text(stringResource(R.string.permissions_shizuku_permission_grant))
                    }
                }
                else -> {}
            } }

            CardWithActions(
                title = title?.let { stringResource(it) } ?: "",
                buttons = { button() },
                modifier = Modifier.padding(8.dp)
            ) {
                description?.let {
                    Text(
                        text = stringResource(it),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Card(
            shape = AppComponentShape,
            modifier = Modifier.padding(8.dp),
            onClick = {
                permissionsViewModel.prefs.fileManagementMethod = FileManagementMethod.SAF.ordinal
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Help,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.permissions_shizuku_warning),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}