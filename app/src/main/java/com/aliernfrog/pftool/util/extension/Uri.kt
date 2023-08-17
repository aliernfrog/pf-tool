package com.aliernfrog.pftool.util.extension

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.aliernfrog.pftool.util.staticutil.UriUtil
import com.lazygeniouz.dfc.file.DocumentFileCompat
import java.io.File


fun Uri.appHasPermissions(context: Context): Boolean {
    val hasPermissions = context.checkUriPermission(
        this,
        android.os.Process.myPid(),
        android.os.Process.myUid(),
        Intent.FLAG_GRANT_READ_URI_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED
    if (!hasPermissions) return false
    val file = DocumentFileCompat.fromTreeUri(context, this)
    return file?.exists() == true
}

fun Uri.cacheFile(context: Context): File? {
    return UriUtil.cacheFile(
        uri = this,
        parentName = "cache",
        context = context
    )
}

fun Uri.resolvePath(): String? {
    val storageRoot = Environment.getExternalStorageDirectory().toString()
    val pathSplit = pathSegments.last().split(":", limit = 2)
    val root = pathSplit.first()
    val filePath = pathSplit.last()
    //TODO remove log
    val resolvedPath = when (root) {
        "primary" -> "$storageRoot/$filePath"
        "home" -> {
            val documentsRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
            "$documentsRoot/$filePath"
        }
        else -> null
    }
    Log.d("uriPathResolver", "uri: $this, path: $resolvedPath")
    return resolvedPath
}

fun Uri.takePersistablePermissions(context: Context) {
    val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    context.grantUriPermission(context.packageName, this, takeFlags)
    context.contentResolver.takePersistableUriPermission(this, takeFlags)
}