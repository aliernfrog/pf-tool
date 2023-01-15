package com.aliernfrog.pftool.data

import com.lazygeniouz.dfc.file.DocumentFileCompat
import java.io.File

data class PFMap(
    val name: String,
    val fileName: String,
    val lastModified: Long? = null,
    val file: File? = null,
    val documentFile: DocumentFileCompat? = null,
    val thumbnailPainterModel: Any? = null
)