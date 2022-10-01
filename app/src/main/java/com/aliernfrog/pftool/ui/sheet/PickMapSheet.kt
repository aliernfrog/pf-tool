package com.aliernfrog.pftool.ui.sheet

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolModalBottomSheet
import com.aliernfrog.pftool.util.FileUtil
import com.aliernfrog.pftool.util.UriToFileUtil
import com.aliernfrog.toptoast.TopToastColorType
import com.aliernfrog.toptoast.TopToastManager
import com.lazygeniouz.filecompat.file.DocumentFileCompat
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PickMapSheet(mapsFile: DocumentFileCompat, topToastManager: TopToastManager, state: ModalBottomSheetState, scrollState: ScrollState, onPathPick: (String) -> Unit, onMapFilePick: (DocumentFileCompat) -> Unit) {
    val context = LocalContext.current
    PFToolModalBottomSheet(title = context.getString(R.string.manageMapsPickMap), state, scrollState) {
        PickFromDeviceButton(topToastManager, state, onPathPick)
        ImportedMaps(mapsFile, state, onMapFilePick)
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
                topToastManager.showToast(context.getString(R.string.warning_couldntConvertToPath), iconDrawableId = R.drawable.exclamation, iconBackgroundColorType = TopToastColorType.ERROR)
            }
        }
    }
    PFToolButton(title = context.getString(R.string.manageMapsPickMapFromDevice), painter = painterResource(id = R.drawable.device), backgroundColor = MaterialTheme.colors.primary, contentColor = MaterialTheme.colors.onPrimary) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("application/zip").putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        launcher.launch(intent)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ImportedMaps(mapsFile: DocumentFileCompat, state: ModalBottomSheetState, onMapFilePick: (DocumentFileCompat) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Text(text = context.getString(R.string.manageMapsPickMapYourMaps), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    val files = mapsFile.listFiles().filter { it.isDirectory() }.sortedBy { it.name.lowercase(Locale.getDefault()) }
    if (files.isEmpty()) {
        NoImportedMaps(context)
    } else {
        files.forEach { file ->
            PFToolButton(title = file.name, description = FileUtil.getLastModified(file, context), painter = painterResource(id = R.drawable.map)) {
                onMapFilePick(file)
                scope.launch { state.hide() }
            }
        }
    }
}

@Composable
private fun NoImportedMaps(context: Context) {
    PFToolColumnRounded(color = MaterialTheme.colors.error) {
        Text(text = context.getString(R.string.manageMapsPickMapNoMapsFound), fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onError)
    }
}