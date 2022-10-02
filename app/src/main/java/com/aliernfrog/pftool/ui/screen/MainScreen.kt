package com.aliernfrog.pftool.ui.screen

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.aliernfrog.pftool.ConfigKey
import com.aliernfrog.pftool.NavRoutes
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.sheet.PermissionSheet
import com.aliernfrog.pftool.ui.sheet.UriPermissionSheet
import com.aliernfrog.pftool.util.FileUtil
import kotlinx.coroutines.launch

private lateinit var mapsDir: String

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(navController: NavController, config: SharedPreferences) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val permissionsSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    val uriPermsSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    mapsDir = config.getString(ConfigKey.KEY_MAPS_DIR, "") ?: ""
    PFToolBaseScaffold(title = LocalContext.current.getString(R.string.app_name), navController = navController) {
        PFToolButton(
            title = context.getString(R.string.manageMaps),
            description = context.getString(R.string.manageMapsDescription),
            painter = painterResource(id = R.drawable.map),
            onClick = {
                checkPermissions(context, onDeny = { scope.launch { permissionsSheetState.show() } }, onGrant = {
                    checkUriPermissions(context, { scope.launch { uriPermsSheetState.show() } }, { navController.navigate(NavRoutes.MAPS) })
                })
            }
        )
        PFToolButton(
            title = context.getString(R.string.options),
            description = context.getString(R.string.optionsDescription),
            painter = painterResource(id = R.drawable.options),
            onClick = {
                navController.navigate(NavRoutes.OPTIONS)
            }
        )
    }
    PermissionSheet(permissionsSheetState)
    UriPermissionSheet(mapsFolder = mapsDir, state = uriPermsSheetState)
}

private fun checkPermissions(context: Context, onDeny: () -> Unit, onGrant: () -> Unit) {
    val hasPerms = if (Build.VERSION.SDK_INT >= 30) Environment.isExternalStorageManager()
    else ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    if (hasPerms) onGrant() else onDeny()
}

private fun checkUriPermissions(context: Context, onDeny: () -> Unit, onGrant: () -> Unit) {
    if (FileUtil.checkUriPermission(mapsDir, context)) onGrant() else onDeny()
}