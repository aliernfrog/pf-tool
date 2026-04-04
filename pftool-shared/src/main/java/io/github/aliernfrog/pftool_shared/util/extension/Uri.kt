package io.github.aliernfrog.pftool_shared.util.extension

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.lazygeniouz.dfc.file.DocumentFileCompat
import io.github.aliernfrog.pftool_shared.util.externalStorageRoot
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.util.TAG
import java.io.File

fun Uri.appHasPermissions(context: Context): Boolean {
    try {
        val hasPermissions = context.checkUriPermission(
            this,
            android.os.Process.myPid(),
            android.os.Process.myUid(),
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermissions) return false
        val file = DocumentFileCompat.fromTreeUri(context, this)
        return file?.exists() == true
    } catch (e: Exception) {
        Log.e(TAG, "Uri/appHasPermissions: Failed to check permissions for uri: $this", e)
        return false
    }
}

fun Uri.cacheFile(context: Context): File? {
    return PFToolSharedUtil.cacheFile(
        uri = this,
        parentName = "cache",
        context = context
    )
}

fun Uri.toPath(): String {
    if (!toString().contains(":")) return toString()
    val pathSplit = pathSegments.last().split(":", limit = 2)
    val root = pathSplit.first()
    val filePath = pathSplit.last()
    val resolvedPath = when (root) {
        //"primary" ->
        "home" -> {
            val documentsRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
            "$documentsRoot/$filePath"
        }
        else -> "$externalStorageRoot$filePath"
    }
    return resolvedPath
}

fun Uri.takePersistablePermissions(context: Context) {
    val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    context.grantUriPermission(context.packageName, this, takeFlags)
    context.contentResolver.takePersistableUriPermission(this, takeFlags)
}