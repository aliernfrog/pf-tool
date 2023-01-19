package com.aliernfrog.pftool.util.extension

import android.content.Context
import com.aliernfrog.pftool.data.PFMap
import com.aliernfrog.pftool.util.staticutil.FileUtil

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