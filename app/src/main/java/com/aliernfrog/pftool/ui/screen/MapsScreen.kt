package com.aliernfrog.pftool.ui.screen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolTextField
import com.aliernfrog.pftool.ui.sheets.PickMapSheet
import com.aliernfrog.pftool.utils.FileUtil
import com.aliernfrog.pftool.utils.ZipUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

private val mapPath = mutableStateOf("")
private val mapNameEdit = mutableStateOf("")
private val mapNameOriginal = mutableStateOf("")
private val recompose = mutableStateOf(false)

private lateinit var mapsDir: String
private lateinit var mapsExportDir: String
private lateinit var mapsDocumentFile: DocumentFile

private lateinit var scope: CoroutineScope
private lateinit var scaffoldState: ScaffoldState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapsScreen(navController: NavController, config: SharedPreferences, mapsTreeDocumentFile: DocumentFile) {
    val context = LocalContext.current
    scope = rememberCoroutineScope()
    scaffoldState = rememberScaffoldState()
    mapsDir = config.getString("mapsDir", "") ?: ""
    mapsExportDir = config.getString("mapsExportDir", "") ?: ""
    mapsDocumentFile = mapsTreeDocumentFile
    val pickMapSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val pickMapSheetScrollState = rememberScrollState()
    PFToolBaseScaffold(title = context.getString(R.string.manageMaps), navController = navController, scaffoldState) {
        PickMapFileButton(pickMapSheetState, pickMapSheetScrollState)
        MapActions()
    }
    PickMapSheet(
        mapsDocumentFile = mapsDocumentFile,
        state = pickMapSheetState,
        scrollState = pickMapSheetScrollState,
        onPathPick = { getMap(it, context = context) },
        onDocumentFilePick = { getMap(documentFile = it, context = context) }
    )
    recompose.value
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PickMapFileButton(pickMapSheetState: ModalBottomSheetState, pickMapSheetScrollState: ScrollState) {
    val context = LocalContext.current
    PFToolButton(
        title = context.getString(R.string.manageMapsPickMap),
        painter = painterResource(id = R.drawable.map),
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
    ) {
        recompose.value = !recompose.value
        scope.launch { openPickMapSheet(pickMapSheetState, pickMapSheetScrollState) }
    }
}

@Composable
private fun MapActions() {
    if (mapPath.value != "") {
        val context = LocalContext.current
        val isImported = mapPath.value.startsWith(mapsDir)
        PFToolColumnRounded(title = context.getString(R.string.manageMapsMapName)) {
            PFToolTextField(
                value = mapNameEdit.value,
                placeholder = { Text(mapNameOriginal.value) },
                onValueChange = { mapNameEdit.value = it },
                singleLine = true
            )
            if (isImported && getMapNameEdit() != mapNameOriginal.value) {
                PFToolButton(
                    title = context.getString(R.string.manageMapsRename),
                    painter = painterResource(id = R.drawable.edit),
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary
                ) {
                    renameChosenMap(context)
                }
            }
        }
        if (!isImported) {
            PFToolButton(
                title = context.getString(R.string.manageMapsImport),
                painter = painterResource(id = R.drawable.download),
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            ) {
                importChosenMap(context)
            }
        }
        if (isImported) {
            PFToolButton(
                title = context.getString(R.string.manageMapsExport),
                description = context.getString(R.string.manageMapsExportDescription),
                painter = painterResource(id = R.drawable.share)) {
                exportChosenMap(context)
            }
            PFToolButton(
                title = context.getString(R.string.manageMapsDelete),
                painter = painterResource(id = R.drawable.trash),
                backgroundColor = MaterialTheme.colors.error,
                contentColor = MaterialTheme.colors.onError
            ) {
                deleteChosenMap(context)
            }
        }
    }
}

private fun getMap(path: String? = null, documentFile: DocumentFile? = null, context: Context) {
    if (path != null) {
        val file = File(path)
        var mapName = file.name
        if (!file.isDirectory) mapName = file.nameWithoutExtension
        if (file.exists()) {
            mapPath.value = file.absolutePath
            mapNameEdit.value = mapName
            mapNameOriginal.value = mapNameEdit.value
        } else {
            scope.launch { scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.warning_fileDoesntExist)) }
        }
    } else if (documentFile != null) {
        var mapName = documentFile.name ?: "map"
        if (!documentFile.isDirectory) mapName = FileUtil.removeExtension(mapName)
        if (documentFile.exists()) {
            mapPath.value = "$mapsDir/$mapName"
            mapNameEdit.value = mapName
            mapNameOriginal.value = mapNameEdit.value
        } else {
            scope.launch { scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.warning_fileDoesntExist)) }
        }
    } else {
        mapPath.value = ""
        mapNameEdit.value = ""
        mapNameOriginal.value = ""
    }
}

private fun renameChosenMap(context: Context) {
    val outputFile = mapsDocumentFile.findFile(getMapNameEdit())
    if (outputFile != null && outputFile.exists()) {
        scope.launch { scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.warning_mapAlreadyExists)) }
    } else {
        mapsDocumentFile.findFile(mapNameOriginal.value)?.renameTo(getMapNameEdit())
        getMap(documentFile = mapsDocumentFile.findFile(getMapNameEdit()), context = context)
        scope.launch { scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.info_done)) }
    }
}

private fun importChosenMap(context: Context) {
    var outputFile = mapsDocumentFile.findFile(getMapNameEdit())
    if (outputFile != null && outputFile.exists()) {
        scope.launch { scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.warning_mapAlreadyExists)) }
    } else {
        outputFile = mapsDocumentFile.createDirectory(getMapNameEdit())
        if (outputFile != null) ZipUtil.unzipMap(mapPath.value, outputFile, context)
        getMap(documentFile = outputFile, context = context)
        scope.launch { scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.info_done)) }
    }
}

private fun exportChosenMap(context: Context) {
    val outputFile = File("${mapsExportDir}/${getMapNameEdit()}.zip")
    if (!outputFile.parentFile?.isDirectory!!) outputFile.parentFile?.mkdirs()
    if (outputFile.exists()) {
        scope.launch { scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.warning_mapAlreadyExists)) }
    } else {
        ZipUtil.zipMap(folder = mapsDocumentFile.findFile(mapNameOriginal.value)!!, zipPath = outputFile.absolutePath, context)
        val snackbarString = "${context.getString(R.string.info_exportedMap)}\n${outputFile.absolutePath}"
        scope.launch {
            when (scaffoldState.snackbarHostState.showSnackbar(snackbarString, context.getString(R.string.action_share))) {
                SnackbarResult.ActionPerformed -> {
                    val intent = FileUtil.shareFile(outputFile.absolutePath, "application/zip", context)
                    context.startActivity(Intent.createChooser(intent, context.getString(R.string.action_share)))
                }
                SnackbarResult.Dismissed -> { }
            }
        }
    }
}

private fun deleteChosenMap(context: Context) {
    mapsDocumentFile.findFile(mapNameOriginal.value)?.delete()
    getMap(context = context)
    scope.launch { scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.info_done)) }
}

private fun getMapNameEdit(): String {
    return mapNameEdit.value.ifBlank { mapNameOriginal.value }
}

@OptIn(ExperimentalMaterialApi::class)
private suspend fun openPickMapSheet(pickMapSheetState: ModalBottomSheetState, pickMapSheetScrollState: ScrollState) {
    pickMapSheetScrollState.scrollTo(0)
    pickMapSheetState.show()
}