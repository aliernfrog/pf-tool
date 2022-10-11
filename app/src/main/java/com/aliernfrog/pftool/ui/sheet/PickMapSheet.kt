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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.aliernfrog.pftool.data.MapsListItem
import com.aliernfrog.pftool.PickMapSheetSegments
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolModalBottomSheet
import com.aliernfrog.pftool.ui.composable.PFToolSegmentedButtons
import com.aliernfrog.pftool.ui.state.MapsState
import com.aliernfrog.pftool.util.FileUtil
import com.aliernfrog.pftool.util.UriToFileUtil
import com.aliernfrog.toptoast.TopToastColorType
import com.aliernfrog.toptoast.TopToastManager
import com.lazygeniouz.filecompat.file.DocumentFileCompat
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun PickMapSheet(mapsState: MapsState, topToastManager: TopToastManager, sheetState: ModalBottomSheetState, scrollState: ScrollState = rememberScrollState(), onPathPick: (String) -> Unit, onMapFilePick: (DocumentFileCompat) -> Unit) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val hideSheet = { scope.launch { sheetState.hide() } }
    PFToolModalBottomSheet(title = context.getString(R.string.manageMapsPickMap), sheetState, scrollState) {
        PickFromDeviceButton(topToastManager, onPathPick)
        Maps(mapsState, { onPathPick(it); hideSheet() }, { onMapFilePick(it); hideSheet() })
    }
    LaunchedEffect(sheetState.isVisible) {
        if (sheetState.isVisible) keyboardController?.hide()
        else scrollState.scrollTo(0)
    }
}

@Composable
private fun PickFromDeviceButton(topToastManager: TopToastManager, onPathPick: (String) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data?.data != null) {
            val convertedPath = UriToFileUtil.getRealFilePath(it.data?.data!!, context)
            if (convertedPath != null) {
                onPathPick(convertedPath)
            } else {
                topToastManager.showToast(context.getString(R.string.warning_couldntConvertToPath), iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
            }
        }
    }
    PFToolButton(title = context.getString(R.string.manageMapsPickMapFromDevice), painter = painterResource(id = R.drawable.device), containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("application/zip").putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        launcher.launch(intent)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Maps(mapsState: MapsState, onPathPick: (String) -> Unit, onMapFilePick: (DocumentFileCompat) -> Unit) {
    val context = LocalContext.current
    var selectedSegment by remember { mutableStateOf(PickMapSheetSegments.IMPORTED) }
    PFToolSegmentedButtons(options = listOf(context.getString(R.string.manageMapsPickMapYourMaps),context.getString(R.string.manageMapsPickMapExportedMaps))) {
        selectedSegment = it
    }
    AnimatedContent(targetState = selectedSegment) {
        Column {
            val maps = if (it == PickMapSheetSegments.IMPORTED) mapsState.importedMaps else mapsState.exportedMaps
            MapsList(maps = maps.value, exportedMaps = it == PickMapSheetSegments.EXPORTED, onPathPick, onMapFilePick)
        }
    }
}

@Composable
private fun MapsList(maps: List<MapsListItem>, exportedMaps: Boolean, onPathPick: (String) -> Unit, onMapFilePick: (DocumentFileCompat) -> Unit) {
    val context = LocalContext.current
    if (maps.isNotEmpty()) {
        maps.forEach { map ->
            PFToolButton(title = map.name, description = FileUtil.lastModifiedFromLong(map.lastModified, context), painter = painterResource(id = R.drawable.map)) {
                if (map.documentFile != null) onMapFilePick(map.documentFile)
                else if (map.file != null) onPathPick(map.file.absolutePath)
            }
        }
    } else {
        PFToolColumnRounded(color = MaterialTheme.colorScheme.error) {
            Text(text = context.getString(if (exportedMaps) R.string.manageMapsPickMapNoExportedMaps else R.string.manageMapsPickMapNoImportedMaps), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onError)
        }
    }
}