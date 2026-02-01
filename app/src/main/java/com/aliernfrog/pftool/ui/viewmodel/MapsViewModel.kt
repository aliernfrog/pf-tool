package com.aliernfrog.pftool.ui.viewmodel

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.domain.AppState
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.domain.MapsState
import com.aliernfrog.pftool.ui.component.widget.media_overlay.ThumbnailToolbarContent
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.pftool.util.staticutil.FileUtil
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.pftool_shared.impl.Progress
import io.github.aliernfrog.pftool_shared.impl.ProgressState
import io.github.aliernfrog.shared.data.MediaOverlayData
import io.github.aliernfrog.shared.impl.ContextUtils
import kotlinx.coroutines.launch

@Suppress("IMPLICIT_CAST_TO_ANY")
class MapsViewModel(
    private val appState: AppState,
    private val mapsState: MapsState,
    private val progressState: ProgressState,
    private val topToastState: TopToastState,
    private val contextUtils: ContextUtils,
    val prefs: PreferenceManager
) : ViewModel() {
    var mapsPendingDelete
        get() = mapsState.mapsPendingDelete
        set(value) { mapsState.mapsPendingDelete = value }

    var customDialogTitleAndText
        get() = mapsState.customDialogTitleAndText
        set(value) { mapsState.customDialogTitleAndText = value }

    private var activeProgress: Progress?
        get() = progressState.currentProgress
        set(value) { progressState.currentProgress = value }

    fun viewMapDetails(map: Any) {
        mapsState.viewMapDetails(map)
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
            appState.navigationBackStack.removeIf {
                it is MapFile && maps.any { map ->
                    map.path == it.path
                }
            }
        }
        loadMaps(context)
        activeProgress = null
    }

    fun openMapThumbnailViewer(map: MapFile) {
        val hasThumbnail = map.thumbnailModel != null
        appState.mediaOverlayData = MediaOverlayData(
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
        )
    }

    fun loadMaps(context: Context) {
        viewModelScope.launch {
            mapsState.loadMaps(context)
        }
    }
}