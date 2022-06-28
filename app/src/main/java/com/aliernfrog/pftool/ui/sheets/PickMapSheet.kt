package com.aliernfrog.pftool.ui.sheets

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.documentfile.provider.DocumentFile
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolRoundedModalBottomSheet
import com.aliernfrog.pftool.utils.FileUtil
import com.aliernfrog.pftool.utils.UriToFileUtil
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PickMapSheet(mapsDocumentFile: DocumentFile, state: ModalBottomSheetState, scrollState: ScrollState, onPathPick: (String) -> Unit, onDocumentFilePick: (DocumentFile) -> Unit) {
    val context = LocalContext.current
    PFToolRoundedModalBottomSheet(title = context.getString(R.string.manageMapsPickMap), state, scrollState) {
        PickFromDeviceButton(state, onPathPick)
        ImportedMaps(mapsDocumentFile, state, onDocumentFilePick)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PickFromDeviceButton(state: ModalBottomSheetState, onPathPick: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data?.data != null) {
            val convertedPath = UriToFileUtil.getRealFilePath(it.data?.data!!, context)
            if (convertedPath != null) {
                onPathPick(convertedPath)
                scope.launch { state.hide() }
            } else {
                Toast.makeText(context, context.getString(R.string.warning_couldntConvertToPath), Toast.LENGTH_SHORT).show()
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
private fun ImportedMaps(mapsDocumentFile: DocumentFile, state: ModalBottomSheetState, onDocumentFilePick: (DocumentFile) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Text(text = context.getString(R.string.manageMapsPickMapYourMaps), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    val files = mapsDocumentFile.listFiles().filter { it.isDirectory }.sortedBy { it.name?.lowercase(Locale.getDefault()) }
    if (files.isEmpty()) {
        NoImportedMaps(context)
    } else {
        files.forEach { file ->
            PFToolButton(title = file.name.toString(), description = FileUtil.getLastModified(file, context), painter = painterResource(id = R.drawable.map)) {
                onDocumentFilePick(file)
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