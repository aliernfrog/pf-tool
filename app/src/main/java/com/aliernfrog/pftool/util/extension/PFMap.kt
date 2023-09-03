package com.aliernfrog.pftool.util.extension

import android.content.Context
import com.aliernfrog.pftool.data.PFMap
import com.aliernfrog.pftool.util.staticutil.FileUtil
import com.lazygeniouz.dfc.file.DocumentFileCompat
import java.io.File

fun PFMap.resolveFile(): Any {
    return this.documentFile ?: this.file!!
}

fun PFMap.resolvePath(): String? {
    return when (val file = this.resolveFile()) {
        is File -> file.absolutePath
        is DocumentFileCompat -> file.uri.toString()
        else -> null
    }
}

fun PFMap.getDetails(context: Context): String? {
    val details = mutableListOf<String>()
    if (this.fileSize != null) details.add(
        "${this.fileSize/1024} KB"
    )
    details.add(
        FileUtil.lastModifiedFromLong(this.lastModified, context)
    )
    val result = if (details.isEmpty()) null else details.joinToString(" | ")
    this.details.value = result
    return result
}

fun PFMap.equalsMap(compare: PFMap): Boolean {
    return resolveFile() == compare.resolveFile()
}