package com.aliernfrog.pftool.ui.screen

import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.canRequestAndroidDataAccess
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.externalStorageRoot
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.component.screen.permissions.PermissionCard
import com.aliernfrog.pftool.ui.component.screen.permissions.ShizukuWarning
import com.aliernfrog.pftool.ui.dialog.NotRecommendedFolderDialog
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.pftool.util.extension.appHasPermissions
import com.aliernfrog.pftool.util.extension.resolvePath
import com.aliernfrog.pftool.util.extension.takePersistablePermissions
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    vararg permissionsData: PermissionData,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    fun getMissingPermissions(): List<PermissionData> {
        return permissionsData.filter {
            !Uri.parse(it.getUri()).appHasPermissions(context)
        }
    }

    var missingPermissions by remember { mutableStateOf(
        getMissingPermissions()
    ) }

    val shizukuRequired = remember {
        missingPermissions.any { data ->
            data.recommendedPath?.startsWith("${externalStorageRoot}Android/data") == true
        } && !canRequestAndroidDataAccess
    }

    Crossfade(targetState = missingPermissions.isEmpty()) { hasPermissions ->
        if (hasPermissions) content()
        else AppScaffold(
            title = stringResource(R.string.permissions)
        ) {
            Permissions(
                shizukuRequired = shizukuRequired,
                missingPermissions = missingPermissions,
                onUpdateState = {
                    missingPermissions = getMissingPermissions()
                }
            )
        }
    }
}

@Composable
private fun Permissions(
    mainViewModel: MainViewModel = getViewModel(),
    shizukuRequired: Boolean,
    missingPermissions: List<PermissionData>,
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
        if (it == null) return@rememberLauncherForActivityResult
        val recommendedPath = activePermissionData?.recommendedPath
        if (recommendedPath != null) {
            val resolvedPath = it.resolvePath()
            val isRecommendedPath = resolvedPath.equals(recommendedPath, ignoreCase = true)
            if (!isRecommendedPath) unrecommendedPathWarningUri = it
            else takePersistableUriPermissions(it)
        } else {
            takePersistableUriPermissions(it)
        }
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
        if (shizukuRequired) item {
            ShizukuWarning(
                onClickGetStarted = {
                    mainViewModel.topToastState.showToast("Soon™")
                }
            )
        }

        items(missingPermissions) { permissionData ->
            PermissionCard(
                permissionData = permissionData,
                onFolderPickRequest = {
                    openFolderPicker(permissionData)
                }
            )
        }
    }

    unrecommendedPathWarningUri?.let { uri ->
        NotRecommendedFolderDialog(
            permissionData = activePermissionData!!,
            onDismissRequest = { unrecommendedPathWarningUri = null },
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