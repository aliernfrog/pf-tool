package com.aliernfrog.pftool.ui.sheet

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.aliernfrog.pftool.PickMapSheetSegments
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolModalBottomSheet
import com.aliernfrog.pftool.ui.composable.PFToolSegmentedButtons
import com.aliernfrog.pftool.util.FileUtil
import com.aliernfrog.pftool.util.UriToFileUtil
import com.aliernfrog.toptoast.TopToastColorType
import com.aliernfrog.toptoast.TopToastManager
import com.lazygeniouz.filecompat.file.DocumentFileCompat
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PickMapSheet(mapsFile: DocumentFileCompat, exportedMapsFile: File, topToastManager: TopToastManager, state: ModalBottomSheetState, scrollState: ScrollState, onPathPick: (String) -> Unit, onMapFilePick: (DocumentFileCompat) -> Unit) {
    val context = LocalContext.current
    PFToolModalBottomSheet(title = context.getString(R.string.manageMapsPickMap), state, scrollState) {
        PickFromDeviceButton(topToastManager, state, onPathPick)
        Maps(mapsFile, exportedMapsFile, state, onPathPick, onMapFilePick)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PickFromDeviceButton(topToastManager: TopToastManager, state: ModalBottomSheetState, onPathPick: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data?.data != null) {
            val convertedPath = UriToFileUtil.getRealFilePath(it.data?.data!!, context)
            if (convertedPath != null) {
                onPathPick(convertedPath)
                scope.launch { state.hide() }
            } else {
                topToastManager.showToast(context.getString(R.string.warning_couldntConvertToPath), iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
            }
        }
    }
    PFToolButton(title = context.getString(R.string.manageMapsPickMapFromDevice), painter = painterResource(id = R.drawable.device), backgroundColor = MaterialTheme.colors.primary, contentColor = MaterialTheme.colors.onPrimary) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("application/zip").putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        launcher.launch(intent)
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
private fun Maps(mapsFile: DocumentFileCompat, exportedMapsFile: File, state: ModalBottomSheetState, onPathPick: (String) -> Unit, onMapFilePick: (DocumentFileCompat) -> Unit) {
    val context = LocalContext.current
    var selectedSegment by remember { mutableStateOf(PickMapSheetSegments.IMPORTED) }
    PFToolSegmentedButtons(options = listOf(context.getString(R.string.manageMapsPickMapYourMaps),context.getString(R.string.manageMapsPickMapExportedMaps))) {
        selectedSegment = it
    }
    AnimatedContent(targetState = selectedSegment) {
        Column {
            when(it) {
                PickMapSheetSegments.IMPORTED -> ImportedMaps(mapsFile, state, onMapFilePick)
                PickMapSheetSegments.EXPORTED -> ExportedMaps(exportedMapsFile, state, onPathPick)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ImportedMaps(mapsFile: DocumentFileCompat, state: ModalBottomSheetState, onMapFilePick: (DocumentFileCompat) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val files = mapsFile.listFiles().filter { it.isDirectory() }.sortedBy { it.name.lowercase() }
    if (files.isNotEmpty()) {
        files.forEach { file ->
            PFToolButton(title = file.name, description = FileUtil.getLastModified(file, context), painter = painterResource(id = R.drawable.map)) {
                onMapFilePick(file)
                scope.launch { state.hide() }
            }
        }
    } else {
        NoMaps(context)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ExportedMaps(exportedMapsFile: File, state: ModalBottomSheetState, onPathPick: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val files = exportedMapsFile.listFiles()?.filter { it.isFile && it.name.lowercase().endsWith(".zip") }?.sortedBy { it.name.lowercase() }
    if (files != null && files.isNotEmpty()) {
        files.forEach { file ->
            PFToolButton(title = file.nameWithoutExtension, description = FileUtil.getLastModified(file, context), painter = painterResource(id = R.drawable.map)) {
                onPathPick(file.absolutePath)
                scope.launch { state.hide() }
            }
        }
    } else {
        NoMaps(context, true)
    }
}

@Composable
private fun NoMaps(context: Context, exportedMaps: Boolean = false) {
    PFToolColumnRounded(color = MaterialTheme.colors.error) {
        Text(text = context.getString(if (exportedMaps) R.string.manageMapsPickMapNoExportedMaps else R.string.manageMapsPickMapNoImportedMaps), fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onError)
    }
}