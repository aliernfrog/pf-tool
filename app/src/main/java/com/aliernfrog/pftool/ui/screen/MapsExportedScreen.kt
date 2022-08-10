package com.aliernfrog.pftool.ui.screen

import android.content.SharedPreferences
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.sheet.ExportedMapSheet
import com.aliernfrog.pftool.utils.FileUtil
import com.aliernfrog.toptoast.TopToastManager
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

private val recompose = mutableStateOf(false)
private lateinit var topToastManager: TopToastManager
private lateinit var mapsExportDir: String
private lateinit var chosenMap: File

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapsExportedScreen(navController: NavController, toastManager: TopToastManager, config: SharedPreferences) {
    val context = LocalContext.current
    topToastManager = toastManager
    mapsExportDir = config.getString("mapsExportDir", "") ?: ""
    val exportedMapSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    PFToolBaseScaffold(title = context.getString(R.string.exportedMaps), navController = navController) {
        ExportedMapsList(exportedMapSheetState)
    }
    ExportedMapSheet(map = if (::chosenMap.isInitialized) chosenMap else null, topToastManager, state = exportedMapSheetState) { recompose.value = !recompose.value }
    recompose.value
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ExportedMapsList(exportedMapSheetState: ModalBottomSheetState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val files = File(mapsExportDir).listFiles()?.filter { it.isFile && it.name.lowercase(Locale.getDefault()).endsWith(".zip") }?.sortedBy { it.name.lowercase(Locale.getDefault()) }
    if (files == null || files.isEmpty()) {
        PFToolColumnRounded(color = MaterialTheme.colors.error) {
            Text(text = context.getString(R.string.exportedMapsNoMaps), fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onError)
        }
    } else {
        PFToolColumnRounded(color = MaterialTheme.colors.primary) {
            Text(text = context.getString(R.string.exportedMapsHint), fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onPrimary)
        }
        files.forEach {file ->
            PFToolButton(title = file.nameWithoutExtension, description = FileUtil.getLastModified(file, context), painter = painterResource(id = R.drawable.map)) {
                chosenMap = file
                recompose.value = !recompose.value
                scope.launch { exportedMapSheetState.show() }
            }
        }
    }
    recompose.value
}