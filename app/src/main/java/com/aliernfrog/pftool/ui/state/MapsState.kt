package com.aliernfrog.pftool.ui.state

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.provider.DocumentsContract
import androidx.compose.runtime.mutableStateOf
import com.aliernfrog.pftool.ConfigKey
import com.aliernfrog.pftool.data.MapsListItem
import com.lazygeniouz.filecompat.file.DocumentFileCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MapsState(config: SharedPreferences) {
    val mapsDir = config.getString(ConfigKey.KEY_MAPS_DIR, ConfigKey.DEFAULT_MAPS_DIR)!!
    val mapsExportDir = config.getString(ConfigKey.KEY_MAPS_EXPORT_DIR, ConfigKey.DEFAULT_MAPS_EXPORT_DIR)!!
    lateinit var mapsFile: DocumentFileCompat
    private val exportedMapsFile = File(mapsExportDir)

    var importedMaps = mutableStateOf(emptyList<MapsListItem>())
    var exportedMaps = mutableStateOf(emptyList<MapsListItem>())
    var lastMapName = mutableStateOf("")

    fun getMapsFile(context: Context): DocumentFileCompat {
        if (::mapsFile.isInitialized) return mapsFile
        val treeId = mapsDir.replace("${Environment.getExternalStorageDirectory()}/", "primary:")
        val treeUri = DocumentsContract.buildTreeDocumentUri("com.android.externalstorage.documents", treeId)
        mapsFile = DocumentFileCompat.fromTreeUri(context, treeUri)!!
        return mapsFile
    }

    suspend fun getImportedMaps() {
        withContext(Dispatchers.IO) {
            val files = mapsFile.listFiles().filter { it.isDirectory() }.sortedBy { it.name.lowercase() }
            val maps = files.map { MapsListItem(it.name, it.name, it.lastModified, null, it) }
            importedMaps.value = maps
        }
    }

    suspend fun getExportedMaps() {
        withContext(Dispatchers.IO) {
            val files = exportedMapsFile.listFiles()?.filter { it.isFile && it.name.lowercase().endsWith(".zip") }?.sortedBy { it.name.lowercase() }
            val maps = files?.map { MapsListItem(it.nameWithoutExtension, it.name, it.lastModified(), it, null) }
            if (maps != null) exportedMaps.value = maps
        }
    }
}