package com.aliernfrog.pftool.util.extension

import android.content.Context
import com.aliernfrog.pftool.data.PFMap
import com.aliernfrog.pftool.util.staticutil.FileUtil
import com.lazygeniouz.dfc.file.DocumentFileCompat
import java.io.File

fun PFMap.resolveFile(): Any {
    return this.documentFile ?: this.file!!
}

fun PFMap.resolvePath(mapsDir: String): String? {
    return when (val file = this.resolveFile()) {
        is File -> file.absolutePath
        // Right now, only imported maps can be DocumentFileCompat
        is DocumentFileCompat -> "$mapsDir/${this.fileName}"
        else -> null
    }
}

fun PFMap.getDetails(context: Context): String? {
    val details = mutableListOf<String>()
    if (this.fileSize != null) details.add(
        "${this.fileSize/1024} KB"
    )
    if (this.lastModified != null) details.add(
        FileUtil.lastModifiedFromLong(this.lastModified, context)
    )
    return if (details.isEmpty()) null
    else details.joinToString(" | ")
}