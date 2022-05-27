package com.aliernfrog.pftool.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.sheets.PermissionSheet

private val hasPermissions = mutableStateOf(true)

@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    PFToolBaseScaffold(title = LocalContext.current.getString(R.string.app_name), navController = navController) {
        PFToolButton(
            title = context.getString(R.string.manageMaps),
            description = context.getString(R.string.manageMapsDescription),
            painter = painterResource(id = R.drawable.map),
            enabled = hasPermissions.value,
            onClick = {
                navController.navigate("maps")
            }
        )
        PFToolButton(
            title = context.getString(R.string.options),
            description = context.getString(R.string.optionsDescription),
            painter = painterResource(id = R.drawable.options),
            onClick = {
                navController.navigate("options")
            }
        )
    }
    if (!checkPermissions(context)) PermissionSheet { hasPermissions.value = true }
}

private fun checkPermissions(context: Context): Boolean {
    hasPermissions.value = if (Build.VERSION.SDK_INT >= 30) Environment.isExternalStorageManager()
    else ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    return hasPermissions.value
}