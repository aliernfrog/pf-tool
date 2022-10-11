package com.aliernfrog.pftool.data

import com.lazygeniouz.filecompat.file.DocumentFileCompat
import java.io.File

data class MapsListItem(
    val name: String,
    val fileName: String,
    val lastModified: Long,
    val file: File?,
    val documentFile: DocumentFileCompat?
)