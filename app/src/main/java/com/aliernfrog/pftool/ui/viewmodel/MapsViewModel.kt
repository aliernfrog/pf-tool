package com.aliernfrog.pftool.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.TAG
import com.aliernfrog.pftool.data.MapActionResult
import com.aliernfrog.pftool.data.MediaViewData
import com.aliernfrog.pftool.data.exists
import com.aliernfrog.pftool.data.mkdirs
import com.aliernfrog.pftool.di.getKoinInstance
import com.aliernfrog.pftool.enum.StorageAccessType
import com.aliernfrog.pftool.impl.FileWrapper
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.impl.Progress
import com.aliernfrog.pftool.impl.ProgressState
import com.aliernfrog.pftool.ui.component.createSheetStateWithDensity
import com.aliernfrog.pftool.ui.component.expressive.ExpressiveButtonRow
import com.aliernfrog.pftool.ui.component.expressive.ExpressiveRowIcon
import com.aliernfrog.pftool.util.extension.showErrorToast
import com.aliernfrog.pftool.util.manager.ContextUtils
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.pftool.util.staticutil.FileUtil
import com.aliernfrog.toptoast.state.TopToastState
import com.lazygeniouz.dfc.file.DocumentFileCompat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Suppress("IMPLICIT_CAST_TO_ANY")
@OptIn(ExperimentalMaterial3Api::class)
class MapsViewModel(
    private val topToastState: TopToastState,
    private val progressState: ProgressState,
    private val contextUtils: ContextUtils,
    val prefs: PreferenceManager,
    context: Context
) : ViewModel() {
    val mapSheetState = createSheetStateWithDensity(skipPartiallyExpanded = true, Density(context))

    val mapsDir: String
        get() = prefs.pfMapsDir.value
    val exportedMapsDir: String
        get() = prefs.exportedMapsDir.value
    lateinit var mapsFile: FileWrapper
        private set
    lateinit var exportedMapsFile: FileWrapper
        private set

    private var lastKnownStorageAccessType = prefs.storageAccessType.value

    var isLoadingMaps by mutableStateOf(true)
    var importedMaps by mutableStateOf(emptyList<MapFile>())
    var exportedMaps by mutableStateOf(emptyList<MapFile>())
    var sharedMaps by mutableStateOf(emptyList<MapFile>())
    var chosenMap by mutableStateOf<MapFile?>(null)
    var mapsPendingDelete by mutableStateOf<List<MapFile>?>(null)
    var mapNameEdit by mutableStateOf("")
    var customDialogTitleAndText: Pair<String, String>? by mutableStateOf(null)

    var activeProgress: Progress?
        get() = progressState.currentProgress
        set(value) { progressState.currentProgress = value }

    suspend fun viewMap(map: Any?) {
        try {
            val mapToChoose = when (map) {
                is MapFile -> map
                is FileWrapper -> MapFile(map)
                else -> if (map == null) null else MapFile(FileWrapper(map))
            }
            if (mapToChoose != null) mapNameEdit = mapToChoose.name
            withContext(Dispatchers.Main) {
                chosenMap = mapToChoose
                mapSheetState.expand()
            }
        } catch (_: CancellationException) {}
        catch (e: Exception) {
            topToastState.showErrorToast()
            Log.e(TAG, "viewMap: ", e)
        }
    }

    suspend fun deletePendingMaps(context: Context) {
        mapsPendingDelete?.let { maps ->
            if (maps.isEmpty()) return@let
            val first = maps.first()
            val total = maps.size
            val isSingle = total == 1

            var passedProgress = 0
            fun getProgress(): Progress {
                return Progress(
                    description = if (isSingle) context.getString(R.string.maps_deleting_single)
                        .replace("{NAME}", first.name)
                    else context.getString(R.string.maps_deleting_multiple)
                        .replace("{DONE}", passedProgress.toString())
                        .replace("{TOTAL}", total.toString()),
                    totalProgress = total.toLong(),
                    passedProgress = passedProgress.toLong()
                )
            }

            activeProgress = getProgress()
            first.runInIOThreadSafe {
                maps.forEach {
                    it.delete()
                    passedProgress++
                    activeProgress = getProgress()
                }
                topToastState.showToast(
                    text = if (isSingle) contextUtils.getString(R.string.maps_deleted_single).replace("{NAME}", first.name)
                    else contextUtils.getString(R.string.maps_deleted_multiple).replace("{COUNT}", maps.size.toString()),
                    icon = Icons.Rounded.Delete
                )
            }
            chosenMap?.path?.let { path ->
                if (maps.map { it.path }.contains(path)) viewMap(null)
            }
        }
        mapsPendingDelete = null
        loadMaps(context)
        activeProgress = null
    }

    fun openMapThumbnailViewer(map: MapFile) {
        val mainViewModel = getKoinInstance<MainViewModel>()
        val hasThumbnail = map.thumbnailModel != null
        mainViewModel.showMediaView(MediaViewData(
            model = map.thumbnailModel,
            title = if (hasThumbnail) map.name else contextUtils.getString(R.string.maps_thumbnail_noThumbnail),
            zoomEnabled = hasThumbnail,
            options = if (!hasThumbnail) null else { {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                ExpressiveButtonRow(
                    title = stringResource(R.string.maps_thumbnail_share),
                    icon = {
                        ExpressiveRowIcon(rememberVectorPainter(Icons.Default.Share))
                    }
                ) {
                    scope.launch {
                        activeProgress = Progress(context.getString(R.string.info_sharing))
                        map.runInIOThreadSafe {
                            FileUtil.shareFiles(map.getThumbnailFile()!!, context = context)
                        }
                        activeProgress = null
                    }
                }
            } }
        ))
    }

    fun resolveMapNameInput(): String {
        return mapNameEdit.ifBlank { chosenMap?.name ?: "" }
    }

    fun showActionFailedDialog(successes: List<Pair<String, MapActionResult>>, fails: List<Pair<String, MapActionResult>>) {
        customDialogTitleAndText = contextUtils.getString(R.string.maps_actionFailed)
            .replace("{SUCCESSES}", successes.size.toString())
            .replace("{FAILS}", fails.size.toString()) to fails.joinToString("\n\n") {
                "${it.first}: ${contextUtils.getString(it.second.message ?: R.string.warning_error)}"
            }
    }

    /**
     * Loads all imported and exported maps. [isLoadingMaps] will be true while this is in action.
     */
    suspend fun loadMaps(context: Context) {
        withContext(Dispatchers.Main) {
            isLoadingMaps = true
        }
        getMapsFile(context)
        getExportedMapsFile(context)
        val imported = fetchImportedMaps()
        val exported = fetchExportedMaps()
        withContext(Dispatchers.Main) {
            importedMaps = imported
            exportedMaps = exported
            isLoadingMaps = false
        }
    }

    /**
     * Gets [DocumentFileCompat] to imported maps folder.
     * Use this before accessing [mapsFile], otherwise the app will crash.
     */
    private fun getMapsFile(context: Context): FileWrapper {
        val isUpToDate = if (!::mapsFile.isInitialized) false
        else if (lastKnownStorageAccessType != prefs.storageAccessType.value) false
        else mapsDir == mapsFile.path
        if (isUpToDate) return mapsFile
        val storageAccessType = prefs.storageAccessType.value
        lastKnownStorageAccessType = storageAccessType
        mapsFile = when (StorageAccessType.entries[storageAccessType]) {
            StorageAccessType.SAF -> {
                val treeUri = mapsDir.toUri()
                DocumentFileCompat.fromTreeUri(context, treeUri)!!
            }
            StorageAccessType.SHIZUKU -> {
                val shizukuViewModel = getKoinInstance<ShizukuViewModel>()
                val file = shizukuViewModel.fileService!!.getFile(mapsDir)!!
                if (!file.exists()) file.mkdirs()
                shizukuViewModel.fileService!!.getFile(mapsDir)
            }
            StorageAccessType.ALL_FILES -> {
                val file = File(mapsDir)
                if (!file.isDirectory) file.mkdirs()
                File(mapsDir)
            }
        }.let { FileWrapper(it) }
        return mapsFile
    }

    /**
     * Gets [DocumentFileCompat] to exported maps folder.
     * Use this before accessing [exportedMapsFile], otherwise the app will crash.
     */
    private fun getExportedMapsFile(context: Context): FileWrapper {
        val isUpToDate = if (!::exportedMapsFile.isInitialized) false
        else if (lastKnownStorageAccessType != prefs.storageAccessType.value) false
        else exportedMapsDir == exportedMapsFile.path
        if (isUpToDate) return exportedMapsFile
        val storageAccessType = prefs.storageAccessType.value
        lastKnownStorageAccessType = storageAccessType
        exportedMapsFile = when (StorageAccessType.entries[storageAccessType]) {
            StorageAccessType.SAF -> {
                val treeUri = exportedMapsDir.toUri()
                DocumentFileCompat.fromTreeUri(context, treeUri)!!
            }
            StorageAccessType.SHIZUKU -> {
                val shizukuViewModel = getKoinInstance<ShizukuViewModel>()
                val file = shizukuViewModel.fileService!!.getFile(exportedMapsDir)
                if (!file.exists()) file.mkdirs()
                shizukuViewModel.fileService!!.getFile(exportedMapsDir)
            }
            StorageAccessType.ALL_FILES -> {
                val file = File(exportedMapsDir)
                if (!file.isDirectory) file.mkdirs()
                File(exportedMapsDir)
            }
        }.let { FileWrapper(it) }
        return exportedMapsFile
    }

    /**
     * Fetches imported maps from [mapsFile].
     */
    private fun fetchImportedMaps(): List<MapFile> {
        return mapsFile.listFiles()
            .filter { !it.isFile }
            .sortedBy { it.name.lowercase() }
            .map { MapFile(it) }
    }

    /**
     * Fetches exported maps from [exportedMapsFile].
     */
    private fun fetchExportedMaps(): List<MapFile> {
        return exportedMapsFile.listFiles()
            .filter { it.isFile && it.name.lowercase().endsWith(".zip") }
            .sortedBy { it.name.lowercase() }
            .map { MapFile(it) }
    }
}