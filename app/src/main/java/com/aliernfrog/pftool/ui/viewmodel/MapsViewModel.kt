package com.aliernfrog.pftool.ui.viewmodel

import android.content.Context
import android.os.Environment
import android.provider.DocumentsContract
import androidx.compose.foundation.ScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PFMap
import com.aliernfrog.pftool.enum.MapImportedState
import com.aliernfrog.pftool.enum.PickMapSheetSegments
import com.aliernfrog.pftool.util.extension.resolvePath
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.pftool.util.staticutil.FileUtil
import com.aliernfrog.pftool.util.staticutil.ZipUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import com.lazygeniouz.dfc.file.DocumentFileCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
class MapsViewModel(
    context: Context,
    val topToastState: TopToastState,
    val prefs: PreferenceManager
) : ViewModel() {
    val pickMapSheetState = SheetState(skipPartiallyExpanded = false, Density(context))
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val scrollState = ScrollState(0)

    val mapsDir = prefs.pfMapsDir
    private val exportedMapsDir = prefs.exportedMapsDir
    private lateinit var mapsFile: DocumentFileCompat
    private val exportedMapsFile = File(exportedMapsDir)

    var importedMaps by mutableStateOf(emptyList<PFMap>())
    var exportedMaps by mutableStateOf(emptyList<PFMap>())
    var mapNameEdit by mutableStateOf("")
    var pendingMapDelete by mutableStateOf<String?>(null)
    var pickMapSheetSelectedSegment by mutableStateOf(PickMapSheetSegments.IMPORTED)

    var chosenMap by mutableStateOf<PFMap?>(null)

    fun chooseMap(map: Any?) {
        var mapToChoose: PFMap? = null
        when (map) {
            is File -> {
                if (map.exists()) mapToChoose = PFMap(
                    name = map.nameWithoutExtension,
                    fileName = map.name,
                    file = map
                ) else fileDoesntExist()
            }
            is DocumentFileCompat -> {
                if (map.exists()) mapToChoose = PFMap(
                    name = if (map.isFile()) FileUtil.removeExtension(map.name) else map.name,
                    fileName = map.name,
                    documentFile = map
                ) else fileDoesntExist()
            }
            is PFMap -> {
                mapToChoose = map
            }
        }

        val mapPath = mapToChoose?.resolvePath(mapsDir) ?: ""
        chosenMap = mapToChoose?.copy(
            importedState = getMapImportedState(mapPath)
        )

        mapToChoose?.name?.let {
            mapNameEdit = it
        }
    }

    suspend fun renameChosenMap(
        newName: String = resolveMapNameInput()
    ) {
        val mapFile = chosenMap?.documentFile ?: return
        val output = mapsFile.findFile(newName)
        if (output != null && output.exists()) fileAlreadyExists()
        else withContext(Dispatchers.IO) {
            mapFile.renameTo(newName)
            chooseMap(mapsFile.findFile(newName))
            topToastState.showToast(R.string.maps_rename_done, Icons.Rounded.Edit)
            fetchImportedMaps()
        }
    }

    suspend fun importChosenMap(context: Context) {
        val mapPath = chosenMap?.file?.absolutePath ?: return
        val mapName = resolveMapNameInput()
        var output = mapsFile.findFile(mapName)
        if (output != null && output.exists()) fileAlreadyExists()
        else withContext(Dispatchers.IO) {
            output = mapsFile.createDirectory(mapName) ?: return@withContext
            ZipUtil.unzipMap(mapPath, output ?: return@withContext, context)
            chooseMap(output)
            topToastState.showToast(R.string.maps_import_done, Icons.Rounded.Download)
            fetchImportedMaps()
        }
    }

    suspend fun exportChosenMap(context: Context) {
        val mapFile = chosenMap?.documentFile ?: return
        val output = File("${exportedMapsDir}/${resolveMapNameInput()}.zip")
        if (output.exists()) fileAlreadyExists()
        else withContext(Dispatchers.IO) {
            if (output.parentFile?.isDirectory != true) output.parentFile?.mkdirs()
            ZipUtil.zipMap(mapFile, output.absolutePath, context)
            chooseMap(output)
            topToastState.showToast(R.string.maps_export_done, icon = Icons.Rounded.Upload)
            fetchExportedMaps()
        }
    }

    suspend fun deleteChosenMap() {
        val map = chosenMap ?: return
        withContext(Dispatchers.IO) {
            if (map.documentFile != null) {
                map.documentFile.delete()
                fetchImportedMaps()
            } else if (map.file != null) {
                map.file.delete()
                fetchExportedMaps()
            }
            chooseMap(null)
            topToastState.showToast(R.string.maps_delete_done, icon = Icons.Rounded.Delete)
        }
    }

    fun resolveMapNameInput(): String {
        return mapNameEdit.ifBlank { chosenMap?.name ?: "" }
    }

    private fun getMapImportedState(path: String): MapImportedState {
        return if (path.startsWith(mapsDir)) MapImportedState.IMPORTED
        else if (path.startsWith(exportedMapsDir)) MapImportedState.EXPORTED
        else MapImportedState.NONE
    }

    fun getMapsFile(context: Context): DocumentFileCompat {
        if (::mapsFile.isInitialized) return mapsFile
        val treeId = mapsDir.replace("${Environment.getExternalStorageDirectory()}/", "primary:")
        val treeUri = DocumentsContract.buildTreeDocumentUri("com.android.externalstorage.documents", treeId)
        mapsFile = DocumentFileCompat.fromTreeUri(context, treeUri)!!
        return mapsFile
    }

    suspend fun fetchAllMaps() {
        fetchImportedMaps()
        fetchExportedMaps()
    }

    private suspend fun fetchImportedMaps() {
        withContext(Dispatchers.IO) {
            importedMaps = mapsFile.listFiles()
                .filter { it.isDirectory() }
                .sortedBy { it.name.lowercase() }
                .map {
                    PFMap(
                        name = it.name,
                        fileName = it.name,
                        fileSize = it.length,
                        lastModified = it.lastModified,
                        file = null,
                        documentFile = it,
                        thumbnailModel = it.findFile("Thumbnail.jpg")?.uri.toString()
                    )
                }
        }
    }

    private suspend fun fetchExportedMaps() {
        withContext(Dispatchers.IO) {
            exportedMaps = (exportedMapsFile.listFiles() ?: emptyArray())
                .filter { it.isFile && it.name.lowercase().endsWith(".zip") }
                .sortedBy { it.name.lowercase() }
                .map {
                    PFMap(
                        name = it.nameWithoutExtension,
                        fileName = it.name,
                        fileSize = it.length(),
                        lastModified = it.lastModified(),
                        file = it,
                        documentFile = null
                    )
                }
        }
    }

    private fun fileAlreadyExists() {
        topToastState.showToast(R.string.maps_alreadyExists, Icons.Rounded.PriorityHigh, TopToastColor.ERROR)
    }

    private fun fileDoesntExist() {
        topToastState.showToast(R.string.warning_fileDoesntExist, Icons.Rounded.PriorityHigh, TopToastColor.ERROR)
    }
}