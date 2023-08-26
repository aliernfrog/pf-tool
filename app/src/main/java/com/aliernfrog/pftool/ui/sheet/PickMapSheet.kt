package com.aliernfrog.pftool.ui.sheet

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PFMap
import com.aliernfrog.pftool.enum.PickMapSheetSegments
import com.aliernfrog.pftool.ui.component.*
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import com.aliernfrog.pftool.util.staticutil.UriUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.enum.TopToastType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.getViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickMapSheet(
    mapsViewModel: MapsViewModel = getViewModel(),
    sheetState: SheetState = mapsViewModel.pickMapSheetState,
    selectedSegment: PickMapSheetSegments = mapsViewModel.pickMapSheetSelectedSegment,
    getShowMapThumbnails: () -> Boolean = { mapsViewModel.prefs.showMapThumbnailsInList },
    onSelectedSegmentChange: (PickMapSheetSegments) -> Unit = {
        mapsViewModel.pickMapSheetSelectedSegment = it
    },
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
            onFail = {
                mapsViewModel.topToastState.showToast(
                    text = R.string.maps_pickMap_failed,
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
            selectedSegment = selectedSegment,
            onSelectedSegmentChange = onSelectedSegmentChange,
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
    onFail: () -> Unit,
    onFilePick: (File) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data?.data != null) scope.launch {
            withContext(Dispatchers.IO) {
                val cachedFile = UriUtil.cacheFile(
                    uri = it.data?.data!!,
                    parentName = "maps",
                    context = context
                )
                if (cachedFile != null) onFilePick(cachedFile)
                else onFail()
            }
        }
    }
    Button(
        onClick = {
            val intent = Intent(Intent.ACTION_GET_CONTENT).setType("application/zip")
            launcher.launch(intent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        ButtonIcon(rememberVectorPainter(Icons.Outlined.FolderZip))
        Text(stringResource(R.string.maps_pickMap_device))
    }
}

@Composable
private fun Maps(
    importedMaps: List<PFMap>,
    exportedMaps: List<PFMap>,
    showMapThumbnails: Boolean,
    selectedSegment: PickMapSheetSegments,
    onSelectedSegmentChange: (PickMapSheetSegments) -> Unit,
    onMapPick: (Any) -> Unit
) {
    SegmentedButtons(
        options = listOf(
            stringResource(R.string.maps_pickMap_imported),
            stringResource(R.string.maps_pickMap_exported)
        ),
        selectedIndex = selectedSegment.ordinal,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        onSelectedSegmentChange(PickMapSheetSegments.values()[it])
    }
    AnimatedContent(targetState = selectedSegment.ordinal) {
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
        ErrorWithIcon(
            error = stringResource(if (isShowingExportedMaps) R.string.maps_pickMap_noExportedMaps else R.string.maps_pickMap_noImportedMaps),
            painter = rememberVectorPainter(Icons.Rounded.LocationOff)
        )
    }
}