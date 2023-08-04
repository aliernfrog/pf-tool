package com.aliernfrog.pftool.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.aliernfrog.pftool.enum.MapImportedState
import com.lazygeniouz.dfc.file.DocumentFileCompat
import java.io.File

data class PFMap(
    val name: String,
    val fileName: String,
    val fileSize: Long? = null,
    val lastModified: Long? = null,
    val file: File? = null,
    val documentFile: DocumentFileCompat? = null,
    val importedState: MapImportedState = MapImportedState.NONE,
    val thumbnailModel: Any? = null,
    var details: MutableState<String?> = mutableStateOf(null),
    val isZip: Boolean = fileName.lowercase().endsWith(".zip")
)