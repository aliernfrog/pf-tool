package com.aliernfrog.pftool.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Suppress("DEPRECATION")
class GeneralUtil {
    companion object {
        @Composable
        fun getNavigationBarHeight(): Dp {
            return WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        }

        @Composable
        fun isKeyboardVisible(): Boolean {
            val navigationHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            return WindowInsets.ime.getBottom(LocalDensity.current).dp >= navigationHeight
        }

        fun getAppVersionName(context: Context): String {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName
        }

        fun getAppVersionCode(context: Context): Int {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionCode
        }

        fun checkStoragePermissions(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= 30) Environment.isExternalStorageManager()
            else ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }
}