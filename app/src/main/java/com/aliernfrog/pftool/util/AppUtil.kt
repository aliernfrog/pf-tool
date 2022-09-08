package com.aliernfrog.pftool.util

import android.content.Context
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

@Suppress("DEPRECATION")
class AppUtil {
    companion object {
        @Composable
        fun getStatusBarHeight(): Dp {
            return WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        }

        @Composable
        fun getNavigationBarHeight(): Dp {
            return WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
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
    }
}