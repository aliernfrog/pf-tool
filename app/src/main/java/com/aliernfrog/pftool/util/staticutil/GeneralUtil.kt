package com.aliernfrog.pftool.util.staticutil

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import com.aliernfrog.pftool.di.appModules
import com.aliernfrog.pftool.ui.activity.MainActivity
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.core.context.GlobalContext.unloadKoinModules

@Suppress("DEPRECATION")
class GeneralUtil {
    companion object {
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

        fun restartApp(context: Context, withModules: Boolean = true) {
            val intent = Intent(context, MainActivity::class.java)
            (context as Activity).finish()
            if (withModules) {
                unloadKoinModules(appModules)
                loadKoinModules(appModules)
            }
            context.startActivity(intent)
        }
    }
}