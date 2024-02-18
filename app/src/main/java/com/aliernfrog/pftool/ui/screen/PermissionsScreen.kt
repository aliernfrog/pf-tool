package com.aliernfrog.pftool.ui.screen

import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.enum.PermissionSetupGuideLevel
import com.aliernfrog.pftool.enum.ShizukuStatus
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.component.AppTopBar
import com.aliernfrog.pftool.ui.component.ButtonIcon
import com.aliernfrog.pftool.ui.component.CardWithActions
import com.aliernfrog.pftool.ui.component.form.DividerRow
import com.aliernfrog.pftool.ui.dialog.ChooseFolderIntroDialog
import com.aliernfrog.pftool.ui.dialog.UnrecommendedFolderDialog
import com.aliernfrog.pftool.ui.theme.AppComponentShape
import com.aliernfrog.pftool.ui.viewmodel.PermissionsViewModel
import com.aliernfrog.pftool.ui.viewmodel.ShizukuViewModel
import com.aliernfrog.pftool.util.extension.appHasPermissions
import com.aliernfrog.pftool.util.extension.requiresAndroidData
import com.aliernfrog.pftool.util.extension.resolvePath
import com.aliernfrog.pftool.util.extension.takePersistablePermissions
import org.koin.androidx.compose.koinViewModel
import rikka.shizuku.Shizuku

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    vararg permissionsData: PermissionData,
    permissionsViewModel: PermissionsViewModel = koinViewModel(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val guideLevel = permissionsViewModel.guideLevel

    fun getMissingPermissions(): List<PermissionData> {
        return permissionsData.filter {
            !Uri.parse(it.getUri()).appHasPermissions(context)
        }
    }

    var missingPermissions by remember { mutableStateOf(
        getMissingPermissions()
    ) }

    Crossfade(targetState = missingPermissions.isEmpty()) { hasPermissions ->
        if (hasPermissions) content()
        else AppScaffold(
            topBar = { AppTopBar(
                title = stringResource(R.string.permissions),
                scrollBehavior = it
            ) }
        ) {
            if (guideLevel >= PermissionSetupGuideLevel.SHIZUKU) ShizukuSetup(
                onResetGuideLevel = {
                    permissionsViewModel.guideLevel = PermissionSetupGuideLevel.MAKE_SURE_FOLDER_EXISTS
                }
            )
            else SAFPermissionsList(
                missingPermissions = missingPermissions,
                guideLevel = guideLevel,
                onPushGuideLevel = { permissionsViewModel.pushGuideLevel() },
                onUpdateState = {
                    missingPermissions = getMissingPermissions()
                }
            )
        }
    }
}

@Composable
private fun SAFPermissionsList(
    missingPermissions: List<PermissionData>,
    guideLevel: PermissionSetupGuideLevel,
    onPushGuideLevel: () -> Unit,
    onUpdateState: () -> Unit
) {
    val context = LocalContext.current
    var activePermissionData by remember { mutableStateOf<PermissionData?>(null) }
    var unrecommendedPathWarningUri by remember { mutableStateOf<Uri?>(null) }

    fun takePersistableUriPermissions(uri: Uri) {
        uri.takePersistablePermissions(context)
        activePermissionData?.onUriUpdate?.invoke(uri)
        onUpdateState()
    }

    val uriPermsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree(), onResult = {
        if (it == null) return@rememberLauncherForActivityResult if (activePermissionData?.requiresAndroidData != true) {}
        else unrecommendedPathWarningUri = Uri.EMPTY

        if (activePermissionData?.forceRecommendedPath == true) {
            val recommendedPath = activePermissionData?.recommendedPath
            val resolvedPath = it.resolvePath()
            val isRecommendedPath = resolvedPath.equals(recommendedPath, ignoreCase = true)
            if (!isRecommendedPath) unrecommendedPathWarningUri = it
            else takePersistableUriPermissions(it)
        } else takePersistableUriPermissions(it)
    })

    fun openFolderPicker(permissionData: PermissionData) {
        val starterUri = if (permissionData.recommendedPath != null) DocumentsContract.buildDocumentUri(
            "com.android.externalstorage.documents",
            "primary:"+permissionData.recommendedPath.removePrefix("${Environment.getExternalStorageDirectory()}/")
        ) else null
        uriPermsLauncher.launch(starterUri)
        activePermissionData = permissionData
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(missingPermissions) { index, permissionData ->
            var introDialogShown by remember { mutableStateOf(false) }
            if (introDialogShown) ChooseFolderIntroDialog(
                permissionData = permissionData,
                onDismissRequest = { introDialogShown = false },
                onConfirm = {
                    openFolderPicker(permissionData)
                    introDialogShown = false
                }
            )

            fun onClick() {
                if (permissionData.recommendedPath != null && permissionData.recommendedPathDescription != null)
                    introDialogShown = true
                else openFolderPicker(permissionData)
            }

            if (index != 0) DividerRow(Modifier.fillMaxWidth())
            ListItem(
                headlineContent = { Text(stringResource(permissionData.title)) },
                supportingContent = {
                    Column(Modifier.fillMaxWidth()) {
                        permissionData.content()

                        val isAndroidData = permissionData.forceRecommendedPath && permissionData.recommendedPath
                            ?.startsWith("${Environment.getExternalStorageDirectory()}/Android/data") == true
                        if (isAndroidData) Guide(
                            level = guideLevel,
                            permissionData = permissionData
                        )

                        Button(
                            onClick = ::onClick,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 4.dp)
                        ) {
                            Text(stringResource(R.string.permissions_chooseFolder))
                        }
                    }
                },
                modifier = Modifier.clickable(onClick = ::onClick)
            )
        }
    }

    unrecommendedPathWarningUri?.let { uri ->
        UnrecommendedFolderDialog(
            permissionData = activePermissionData!!,
            chosenUri = uri,
            onDismissRequest = { unrecommendedPathWarningUri = null },
            onFolderDoesNotExist = {
                unrecommendedPathWarningUri = null
                onPushGuideLevel()
            },
            onUseUnrecommendedFolderRequest = {
                takePersistableUriPermissions(uri)
                unrecommendedPathWarningUri = null
            },
            onChooseFolderRequest = {
                activePermissionData?.let { openFolderPicker(it) }
                unrecommendedPathWarningUri = null
            }
        )
    }
}

@Composable
private fun Guide(
    level: PermissionSetupGuideLevel,
    permissionData: PermissionData
) {
    val title = level.title
    val description = if (level == PermissionSetupGuideLevel.MAKE_SURE_FOLDER_EXISTS) permissionData.createFolderHint
    else level.description

    if (title != null || description != null) Card(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            title?.let {
                Text(
                    text = stringResource(it),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            description?.let {
                Text(
                    text = stringResource(it)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                level.buttons()
            }
        }
    }
}

@Composable
private fun ShizukuSetup(
    shizukuViewModel: ShizukuViewModel = koinViewModel(),
    onResetGuideLevel: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        shizukuViewModel.checkAvailability(context)
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
                onResetGuideLevel()
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