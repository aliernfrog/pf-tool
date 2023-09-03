package com.aliernfrog.pftool.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.ScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PFMap
import com.aliernfrog.pftool.enum.MapImportedState
import com.aliernfrog.pftool.util.extension.cacheFile
import com.aliernfrog.pftool.util.extension.nameWithoutExtension
import com.aliernfrog.pftool.util.extension.resolveFile
import com.aliernfrog.pftool.util.extension.resolvePath
import com.aliernfrog.pftool.util.extension.size
import com.aliernfrog.pftool.util.manager.ContextUtils
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
    private val topToastState: TopToastState,
    private val contextUtils: ContextUtils,
    val prefs: PreferenceManager
) : ViewModel() {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val scrollState = ScrollState(0)

    private val mapsDir: String get() { return prefs.pfMapsDir }
    private val exportedMapsDir: String get() { return prefs.exportedMapsDir }
    private lateinit var mapsFile: DocumentFileCompat
    private lateinit var exportedMapsFile: DocumentFileCompat

    var importedMaps by mutableStateOf(emptyList<PFMap>())
    var exportedMaps by mutableStateOf(emptyList<PFMap>())
    var mapNameEdit by mutableStateOf("")
    var pendingMapDelete by mutableStateOf<String?>(null)
    var chosenMap by mutableStateOf<PFMap?>(null)

    /**
     * Maps array which can be used as default of a vararg argument, such as the one in [deleteMap].
     */
    private val mapsArray get() = run {
        val chosen = chosenMap
        if (chosen == null) emptyArray()
        else arrayOf(chosen)
    }

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

        val mapPath = mapToChoose?.resolvePath() ?: ""
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
            topToastState.showToast(
                text = contextUtils.getString(R.string.maps_rename_done).replace("{NAME}", newName),
                icon = Icons.Rounded.Edit
            )
            fetchImportedMaps()
        }
    }

    suspend fun importChosenMap(context: Context) {
        val zipPath = when (val file = chosenMap?.resolveFile() ?: return) {
            is File -> file.absolutePath
            is DocumentFileCompat -> file.uri.cacheFile(context)?.absolutePath
            else -> null
        } ?: return
        val mapName = resolveMapNameInput()
        var output = mapsFile.findFile(mapName)
        if (output != null && output.exists()) fileAlreadyExists()
        else withContext(Dispatchers.IO) {
            output = mapsFile.createDirectory(mapName) ?: return@withContext
            ZipUtil.unzipMap(zipPath, output ?: return@withContext, context)
            chooseMap(output)
            topToastState.showToast(
                text = contextUtils.getString(R.string.maps_import_done).replace("{NAME}", mapName),
                icon = Icons.Rounded.Download
            )
            fetchImportedMaps()
        }
    }

    suspend fun exportChosenMap(context: Context) {
        val mapFile = chosenMap?.documentFile ?: return
        val mapName = resolveMapNameInput()
        val zipFileName = "$mapName.zip"
        var output = exportedMapsFile.findFile(zipFileName)
        if (output?.exists() == true) fileAlreadyExists()
        else withContext(Dispatchers.IO) {
            output = exportedMapsFile.createFile("", zipFileName) ?: return@withContext
            ZipUtil.zipMap(mapFile, output ?: return@withContext, context)
            chooseMap(output)
            topToastState.showToast(
                text = contextUtils.getString(R.string.maps_export_done).replace("{NAME}", mapName),
                icon = Icons.Rounded.Upload
            )
            fetchExportedMaps()
        }
    }

    /**
     * Deletes given [maps] and produces a TopToast based on result.
     * @param unselectChosenMap whether to unselect chosen map after finishing.
     */
    suspend fun deleteMap(
        vararg maps: PFMap = mapsArray,
        unselectChosenMap: Boolean = maps.size == 1
    ) {
        val isSingle = maps.size == 1
        withContext(Dispatchers.IO) {
            maps.forEach {
                it.file?.delete()
                it.documentFile?.delete()
            }
            if (unselectChosenMap) chooseMap(null)
            topToastState.showToast(
                text = if (isSingle) contextUtils.getString(R.string.maps_delete_done).replace("{NAME}", maps.first().name)
                    else contextUtils.getString(R.string.maps_delete_multiple).replace("{COUNT}", maps.size.toString()),
                icon = Icons.Rounded.Delete
            )
            fetchAllMaps()
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

    private fun getMapsFile(context: Context): DocumentFileCompat {
        val isUpToDate = if (!::mapsFile.isInitialized) false
        else {
            val updatedPath = mapsFile.uri.resolvePath()
            val existingPath = Uri.parse(mapsDir).resolvePath()
            updatedPath == existingPath
        }
        if (isUpToDate) return mapsFile
        val treeUri = Uri.parse(mapsDir)
        mapsFile = DocumentFileCompat.fromTreeUri(context, treeUri)!!
        return mapsFile
    }

    private fun getExportedMapsFile(context: Context): DocumentFileCompat {
        val isUpToDate = if (!::exportedMapsFile.isInitialized) false
        else {
            val updatedPath = exportedMapsFile.uri.resolvePath()
            val existingPath = Uri.parse(exportedMapsDir).resolvePath()
            updatedPath == existingPath
        }
        if (isUpToDate) return exportedMapsFile
        val treeUri = Uri.parse(exportedMapsDir)
        exportedMapsFile = DocumentFileCompat.fromTreeUri(context, treeUri)!!
        return exportedMapsFile
    }

    suspend fun loadMaps(context: Context) {
        getMapsFile(context)
        getExportedMapsFile(context)
        fetchAllMaps()
    }

    private suspend fun fetchAllMaps() {
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
                        fileSize = it.size,
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
            exportedMaps = exportedMapsFile.listFiles()
                .filter { it.isFile() && it.name.lowercase().endsWith(".zip") }
                .sortedBy { it.name.lowercase() }
                .map {
                    PFMap(
                        name = it.nameWithoutExtension,
                        fileName = it.name,
                        fileSize = it.size,
                        lastModified = it.lastModified,
                        file = null,
                        documentFile = it
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