package com.aliernfrog.pftool.ui.sheets

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
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
import com.aliernfrog.pftool.ui.composable.PFToolRoundedModalBottomSheet
import com.aliernfrog.pftool.utils.UriToFileUtil
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PickMapSheet(mapsFolder: String, state: ModalBottomSheetState, onMapPick: (String) -> Unit) {
    val context = LocalContext.current
    PFToolRoundedModalBottomSheet(title = context.getString(R.string.manageMapsPickMap), state) {
        PickFromDeviceButton(state, onMapPick)
        ImportedMaps(mapsFolder, state, onMapPick)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PickFromDeviceButton(state: ModalBottomSheetState, onMapPick: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data?.data != null) {
            val convertedPath = UriToFileUtil.getRealFilePath(it.data?.data!!, context)
            if (convertedPath != null) {
                onMapPick(convertedPath)
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
private fun ImportedMaps(mapsFolder: String, state: ModalBottomSheetState, onMapPick: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val files = File(mapsFolder).listFiles()
    files?.let { Arrays.sort(it) }
    files?.filter { it.isDirectory }
    Text(text = context.getString(R.string.manageMapsPickMapYourMaps), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    if (files == null || files.isEmpty()) {
        PFToolColumnRounded(color = MaterialTheme.colors.error) {
            Text(text = context.getString(R.string.manageMapsPickMapNoMapsFound), fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onError)
        }
    } else {
        files.forEach {file ->
            PFToolButton(title = file.name, painter = painterResource(id = R.drawable.map)) {
                onMapPick(file.absolutePath)
                scope.launch { state.hide() }
            }
        }
    }
}