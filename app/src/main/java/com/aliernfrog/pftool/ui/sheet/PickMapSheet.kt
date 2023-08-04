package com.aliernfrog.pftool.ui.sheet

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PFMap
import com.aliernfrog.pftool.enum.PickMapSheetSegments
import com.aliernfrog.pftool.ui.component.*
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import com.aliernfrog.pftool.util.staticutil.UriToFileUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.enum.TopToastType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.getViewModel
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PickMapSheet(
    mapsViewModel: MapsViewModel = getViewModel(),
    sheetState: ModalBottomSheetState = mapsViewModel.pickMapSheetState,
    getShowMapThumbnails: () -> Boolean = { mapsViewModel.prefs.showMapThumbnailsInList },
    onMapPick: (map: Any) -> Boolean
) {
    val scope = rememberCoroutineScope()
    var mapThumbnailsShown by remember { mutableStateOf(getShowMapThumbnails()) }

    fun pickMap(map: Any) {
        if (onMapPick(map)) scope.launch {
            sheetState.hide()
        }
    }

    AppModalBottomSheet(
        title = stringResource(R.string.maps_pickMap),
        sheetState = sheetState
    ) {
        PickFromDeviceButton(
            onPathConversionFail = {
                mapsViewModel.topToastState.showToast(
                    text = R.string.warning_couldntConvertToPath,
                    icon = Icons.Rounded.PriorityHigh,
                    iconTintColor = TopToastColor.ERROR,
                    type = TopToastType.ANDROID
                )
            },
            onFilePick = {
                pickMap(it)
            }
        )
        Maps(
            importedMaps = mapsViewModel.importedMaps,
            exportedMaps = mapsViewModel.exportedMaps,
            showMapThumbnails = mapThumbnailsShown,
            onMapPick = {
                pickMap(it)
            }
        )
    }

    LaunchedEffect(sheetState.isVisible) {
        if (sheetState.isVisible) mapThumbnailsShown = getShowMapThumbnails()
    }
}

@Composable
private fun PickFromDeviceButton(
    onPathConversionFail: () -> Unit,
    onFilePick: (File) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data?.data != null) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    val convertedPath = UriToFileUtil.getRealFilePath(it.data?.data!!, context)
                    if (convertedPath != null) onFilePick(File(convertedPath))
                    else onPathConversionFail()
                }
            }
        }
    }
    ButtonRounded(
        title = stringResource(R.string.maps_pickMap_device),
        painter = rememberVectorPainter(Icons.Rounded.Folder),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("application/zip")
        launcher.launch(intent)
    }
}

@Composable
private fun Maps(
    importedMaps: List<PFMap>,
    exportedMaps: List<PFMap>,
    showMapThumbnails: Boolean,
    onMapPick: (Any) -> Unit
) {
    var selectedSegment by remember { mutableIntStateOf(PickMapSheetSegments.IMPORTED.ordinal) }
    SegmentedButtons(
        options = listOf(
            stringResource(R.string.maps_pickMap_imported),
            stringResource(R.string.maps_pickMap_exported)
        ),
        initialIndex = selectedSegment
    ) {
        selectedSegment = it
    }
    AnimatedContent(targetState = selectedSegment) {
        Column {
            val maps = if (it == PickMapSheetSegments.IMPORTED.ordinal) importedMaps else exportedMaps
            MapsList(
                maps = maps,
                isShowingExportedMaps = it == PickMapSheetSegments.EXPORTED.ordinal,
                showMapThumbnails = showMapThumbnails,
                onMapPick = onMapPick
            )
        }
    }
}

@Composable
private fun MapsList(
    maps: List<PFMap>,
    isShowingExportedMaps: Boolean,
    showMapThumbnails: Boolean,
    onMapPick: (Any) -> Unit
) {
    if (maps.isNotEmpty()) {
        maps.forEach { map ->
            MapButton(map, showMapThumbnail = showMapThumbnails) {
                onMapPick(map)
            }
        }
    } else {
        ColumnRounded(color = MaterialTheme.colorScheme.error) {
            Text(
                text = stringResource(
                    if (isShowingExportedMaps) R.string.maps_pickMap_noExportedMaps
                    else R.string.maps_pickMap_noImportedMaps
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onError
            )
        }
    }
}