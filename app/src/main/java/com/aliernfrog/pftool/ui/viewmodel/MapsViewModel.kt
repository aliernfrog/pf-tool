package com.aliernfrog.pftool.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.TAG
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.ui.component.widget.media_overlay.ThumbnailToolbarContent
import com.aliernfrog.pftool.util.extension.showErrorToast
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.pftool.util.staticutil.FileUtil
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.pftool_shared.data.MapActionResult
import io.github.aliernfrog.pftool_shared.impl.FileWrapper
import io.github.aliernfrog.pftool_shared.impl.Progress
import io.github.aliernfrog.pftool_shared.impl.ProgressState
import io.github.aliernfrog.pftool_shared.repository.MapRepository
import io.github.aliernfrog.shared.data.MediaOverlayData
import io.github.aliernfrog.shared.di.getKoinInstance
import io.github.aliernfrog.shared.impl.ContextUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Suppress("IMPLICIT_CAST_TO_ANY")
class MapsViewModel(
    private val topToastState: TopToastState,
    private val progressState: ProgressState,
    private val contextUtils: ContextUtils,
    private val mainViewModel: MainViewModel,
    private val mapRepository: MapRepository,
    val prefs: PreferenceManager
) : ViewModel() {
    val mapsDir: String
        get() = prefs.pfMapsDir.value
    val exportedMapsDir: String
        get() = prefs.exportedMapsDir.value
    var mapsPendingDelete by mutableStateOf<List<MapFile>?>(null)
    var customDialogTitleAndText: Pair<String, String>? by mutableStateOf(null)

    var activeProgress: Progress?
        get() = progressState.currentProgress
        set(value) { progressState.currentProgress = value }

    fun viewMapDetails(map: Any) {
        try {
            val mapFile = when (map) {
                is MapFile -> map
                is FileWrapper -> MapFile(map)
                else -> MapFile(FileWrapper(map))
            }
            mainViewModel.navigationBackStack.add(mapFile)
            if (!mainViewModel.prefs.stackupMaps.value) {
                mainViewModel.navigationBackStack.removeIf {
                    it is MapFile && it.path != mapFile.path
                }
            }
        } catch (_: CancellationException) {}
        catch (e: Exception) {
            topToastState.showErrorToast()
            Log.e(TAG, "viewMapDetails: ", e)
        }
    }

    suspend fun deletePendingMaps(context: Context) {
        mapsPendingDelete?.let { maps ->
            mapsPendingDelete = null
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
            mainViewModel.navigationBackStack.removeIf {
                it is MapFile && maps.any { map ->
                    map.path == it.path
                }
            }
        }
        loadMaps(context)
        activeProgress = null
    }

    fun openMapThumbnailViewer(map: MapFile) {
        // TODO remove MainViewModel dependency
        val mainViewModel = getKoinInstance<MainViewModel>()
        val hasThumbnail = map.thumbnailModel != null
        mainViewModel.showMediaOverlay(MediaOverlayData(
            model = map.thumbnailModel,
            title = if (hasThumbnail) map.name else contextUtils.getString(R.string.maps_thumbnail_noThumbnail),
            zoomEnabled = hasThumbnail,
            toolbarContent = {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                ThumbnailToolbarContent(
                    onShareRequest = {
                        scope.launch {
                            activeProgress = Progress(context.getString(R.string.info_sharing))
                            map.runInIOThreadSafe {
                                FileUtil.shareFiles(map.getThumbnailFile()!!, context = context)
                            }
                            activeProgress = null
                        }
                    }
                )
            }
        ))
    }

    fun showActionFailedDialog(successes: List<Pair<String, MapActionResult>>, fails: List<Pair<String, MapActionResult>>) {
        customDialogTitleAndText = contextUtils.getString(R.string.maps_actionFailed)
            .replace("{SUCCESSES}", successes.size.toString())
            .replace("{FAILS}", fails.size.toString()) to fails.joinToString("\n\n") {
                "${it.first}: ${contextUtils.getString(it.second.message ?: R.string.warning_error)}"
            }
    }

    fun loadMaps(context: Context) {
        viewModelScope.launch {
            mapRepository.reloadMaps(context)
        }
    }

    fun getMapsFile(context: Context): FileWrapper? {
        return mapRepository.getImportedMapsFile(context)
    }

    fun getExportedMapsFile(context: Context): FileWrapper? {
        return mapRepository.getExportedMapsFile(context)
    }

    fun setSharedMaps(maps: List<MapFile>) {
        mapRepository.setSharedMaps(maps)
    }
}