package com.aliernfrog.pftool.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.TAG
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.util.extension.resolvePath
import com.aliernfrog.pftool.util.extension.showErrorToast
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState
import com.lazygeniouz.dfc.file.DocumentFileCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class MapsViewModel(
    private val topToastState: TopToastState,
    val prefs: PreferenceManager
) : ViewModel() {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val scrollState = ScrollState(0)

    val mapsDir: String get() { return prefs.pfMapsDir }
    val exportedMapsDir: String get() { return prefs.exportedMapsDir }
    lateinit var mapsFile: DocumentFileCompat
        private set
    lateinit var exportedMapsFile: DocumentFileCompat
        private set

    var isLoadingMaps by mutableStateOf(true)
    var importedMaps by mutableStateOf(emptyList<MapFile>())
    var exportedMaps by mutableStateOf(emptyList<MapFile>())
    var chosenMap by mutableStateOf<MapFile?>(null)
    var pendingMapDelete by mutableStateOf<MapFile?>(null)
    var mapNameEdit by mutableStateOf("")
    var mapListShown by mutableStateOf(true)
    val mapListBackButtonShown
        get() = chosenMap != null

    fun chooseMap(map: Any?) {
        try {
            val mapToChoose = when (map) {
                is MapFile -> map
                else -> if (map == null) null else MapFile(map)
            }

            if (mapToChoose != null) mapNameEdit = mapToChoose.name
            chosenMap = mapToChoose
        } catch (e: Exception) {
            topToastState.showErrorToast()
            Log.e(TAG, "chooseMap: ", e)
        }
    }

    fun resolveMapNameInput(): String {
        return mapNameEdit.ifBlank { chosenMap?.name ?: "" }
    }

    /**
     * Loads all imported and exported maps. [isLoadingMaps] will be true while this is in action.
     */
    suspend fun loadMaps(context: Context) {
        isLoadingMaps = true
        getMapsFile(context)
        getExportedMapsFile(context)
        fetchImportedMaps()
        fetchExportedMaps()
        isLoadingMaps = false
    }

    /**
     * Gets [DocumentFileCompat] to imported maps folder.
     * Use this before accessing [mapsFile], otherwise the app will crash.
     */
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

    /**
     * Gets [DocumentFileCompat] to exported maps folder.
     * Use this before accessing [exportedMapsFile], otherwise the app will crash.
     */
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

    /**
     * Fetches imported maps from [mapsFile].
     */
    private suspend fun fetchImportedMaps() {
        withContext(Dispatchers.IO) {
            importedMaps = mapsFile.listFiles()
                .filter { it.isDirectory() }
                .sortedBy { it.name.lowercase() }
                .map { file ->
                    MapFile(file)
                }
        }
    }

    /**
     * Fetches exported maps from [exportedMapsFile].
     */
    private suspend fun fetchExportedMaps() {
        withContext(Dispatchers.IO) {
            exportedMaps = exportedMapsFile.listFiles()
                .filter { it.isFile() && it.name.lowercase().endsWith(".zip") }
                .sortedBy { it.name.lowercase() }
                .map { file ->
                    MapFile(file)
                }
        }
    }
}