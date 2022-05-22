package com.aliernfrog.pftool.ui.screen

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.sheets.PickMapSheet
import com.aliernfrog.pftool.utils.FileUtil
import com.aliernfrog.pftool.utils.ZipUtil
import kotlinx.coroutines.launch
import java.io.File

private val mapPath = mutableStateOf("")
private val mapNameEdit = mutableStateOf("")
private val mapNameOriginal = mutableStateOf("")

private val mapsBase = "${Environment.getExternalStorageDirectory()}/Documents/PFTool/unzipTest"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapsScreen(navController: NavController) {
    val context = LocalContext.current
    val pickMapSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    PFToolBaseScaffold(title = context.getString(R.string.manageMaps), navController = navController) {
        PickMapFileButton(pickMapSheetState)
        MapActions()
    }
    PickMapSheet(mapsBase, pickMapSheetState) { getMap(it, context) }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PickMapFileButton(pickMapSheetState: ModalBottomSheetState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    PFToolButton(
        title = context.getString(R.string.manageMapsPickMap),
        painter = painterResource(id = R.drawable.map),
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
    ) {
        scope.launch { pickMapSheetState.show() }
    }
}

@Composable
private fun MapActions() {
    if (mapPath.value != "") {
        val context = LocalContext.current
        PFToolColumnRounded(title = context.getString(R.string.manageMapsMapName)) {
            OutlinedTextField(
                value = mapNameEdit.value,
                placeholder = { Text(mapNameOriginal.value) },
                onValueChange = { mapNameEdit.value = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        PFToolButton(
            title = context.getString(R.string.manageMapsImport),
            painter = painterResource(id = R.drawable.download),
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary) {
            importChosenMap(context)
        }
    }
}

private fun getMap(path: String, context: Context) {
    val file = File(path)
    if (file.exists()) {
        mapPath.value = file.absolutePath
        mapNameEdit.value = FileUtil.removeExtension(file.name)
        mapNameOriginal.value = mapNameEdit.value
    } else {
        Toast.makeText(context, context.getString(R.string.warning_fileDoesntExist), Toast.LENGTH_SHORT).show()
    }
}

private fun importChosenMap(context: Context) {
    val outputFile = File("${mapsBase}/${mapNameEdit.value}")
    if (outputFile.exists()) {
        Toast.makeText(context, context.getString(R.string.warning_mapAlreadyExists), Toast.LENGTH_SHORT).show()
    } else {
        ZipUtil.unzip(mapPath.value, outputFile.absolutePath)
        Toast.makeText(context, context.getString(R.string.info_done), Toast.LENGTH_SHORT).show()
    }
}