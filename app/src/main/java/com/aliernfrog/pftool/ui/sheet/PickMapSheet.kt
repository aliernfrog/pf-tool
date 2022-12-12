package com.aliernfrog.pftool.ui.sheet

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.aliernfrog.pftool.PickMapSheetSegments
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.MapsListItem
import com.aliernfrog.pftool.state.MapsState
import com.aliernfrog.pftool.ui.composable.*
import com.aliernfrog.pftool.util.UriToFileUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import com.lazygeniouz.filecompat.file.DocumentFileCompat
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun PickMapSheet(
    mapsState: MapsState,
    topToastState: TopToastState,
    sheetState: ModalBottomSheetState,
    scrollState: ScrollState = rememberScrollState(),
    showMapThumbnails: Boolean = true,
    onFilePick: (File) -> Unit,
    onDocumentFilePick: (DocumentFileCompat) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val hideSheet = { scope.launch { sheetState.hide() } }
    PFToolModalBottomSheet(title = stringResource(R.string.manageMapsPickMap), sheetState, scrollState) {
        PickFromDeviceButton(topToastState) { onFilePick(it); hideSheet() }
        Maps(mapsState, showMapThumbnails, { onFilePick(it); hideSheet() }, { onDocumentFilePick(it); hideSheet() })
    }
    LaunchedEffect(sheetState.isVisible) {
        if (sheetState.isVisible) keyboardController?.hide()
        else scrollState.scrollTo(0)
    }
}

@Composable
private fun PickFromDeviceButton(topToastState: TopToastState, onFilePick: (File) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data?.data != null) {
            val convertedPath = UriToFileUtil.getRealFilePath(it.data?.data!!, context)
            if (convertedPath != null) onFilePick(File(convertedPath))
            else topToastState.showToast(context.getString(R.string.warning_couldntConvertToPath), iconImageVector = Icons.Rounded.PriorityHigh, iconTintColor = TopToastColor.ERROR)
        }
    }
    PFToolButton(title = context.getString(R.string.manageMapsPickMapFromDevice), painter = rememberVectorPainter(Icons.Default.Folder), containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("application/zip").putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        launcher.launch(intent)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Maps(mapsState: MapsState, showMapThumbnails: Boolean, onFilePick: (File) -> Unit, onDocumentFilePick: (DocumentFileCompat) -> Unit) {
    val context = LocalContext.current
    var selectedSegment by remember { mutableStateOf(PickMapSheetSegments.IMPORTED) }
    PFToolSegmentedButtons(options = listOf(context.getString(R.string.manageMapsPickMapYourMaps),context.getString(R.string.manageMapsPickMapExportedMaps))) {
        selectedSegment = it
    }
    AnimatedContent(targetState = selectedSegment) {
        Column {
            val maps = if (it == PickMapSheetSegments.IMPORTED) mapsState.importedMaps else mapsState.exportedMaps
            MapsList(maps = maps.value, showMapThumbnails, exportedMaps = it == PickMapSheetSegments.EXPORTED, onFilePick, onDocumentFilePick)
        }
    }
}

@Composable
private fun MapsList(maps: List<MapsListItem>, showMapThumbnails: Boolean, exportedMaps: Boolean, onFilePick: (File) -> Unit, onDocumentFilePick: (DocumentFileCompat) -> Unit) {
    val context = LocalContext.current
    if (maps.isNotEmpty()) {
        maps.forEach { map ->
            PFToolMapButton(map, showMapThumbnail = showMapThumbnails) {
                if (map.documentFile != null) onDocumentFilePick(map.documentFile)
                else if (map.file != null) onFilePick(map.file)
            }
        }
    } else {
        PFToolColumnRounded(color = MaterialTheme.colorScheme.error) {
            Text(text = context.getString(if (exportedMaps) R.string.manageMapsPickMapNoExportedMaps else R.string.manageMapsPickMapNoImportedMaps), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onError)
        }
    }
}