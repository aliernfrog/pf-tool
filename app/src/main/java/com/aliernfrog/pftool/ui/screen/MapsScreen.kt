package com.aliernfrog.pftool.ui.screen

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.aliernfrog.pftool.ConfigKey
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolTextField
import com.aliernfrog.pftool.ui.sheet.DeleteMapSheet
import com.aliernfrog.pftool.ui.sheet.PickMapSheet
import com.aliernfrog.pftool.util.FileUtil
import com.aliernfrog.pftool.util.ZipUtil
import com.aliernfrog.toptoast.TopToastColorType
import com.aliernfrog.toptoast.TopToastManager
import com.lazygeniouz.filecompat.file.DocumentFileCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

private val mapPath = mutableStateOf("")
private val mapNameEdit = mutableStateOf("")
private val mapNameOriginal = mutableStateOf("")
private val mapIsUri = mutableStateOf(false)
private val recompose = mutableStateOf(false)

private lateinit var mapsDir: String
private lateinit var mapsExportDir: String

private lateinit var scope: CoroutineScope
private lateinit var topToastManager: TopToastManager

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapsScreen(navController: NavController, toastManager: TopToastManager, config: SharedPreferences, mapsFile: DocumentFileCompat) {
    val context = LocalContext.current
    scope = rememberCoroutineScope()
    topToastManager = toastManager
    mapsDir = config.getString(ConfigKey.KEY_MAPS_DIR, "") ?: ""
    mapsExportDir = config.getString(ConfigKey.KEY_MAPS_EXPORT_DIR, "") ?: ""
    val pickMapSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val deleteMapSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    val pickMapSheetScrollState = rememberScrollState()
    PFToolBaseScaffold(title = context.getString(R.string.manageMaps), navController = navController) {
        PickMapFileButton(pickMapSheetState, pickMapSheetScrollState)
        MapActions(mapsFile, deleteMapSheetState)
    }
    PickMapSheet(
        mapsFile = mapsFile,
        exportedMapsFile = File(mapsExportDir),
        topToastManager = topToastManager,
        state = pickMapSheetState,
        scrollState = pickMapSheetScrollState,
        onPathPick = { getMap(it, context = context) },
        onMapFilePick = { getMap(mapFile = it, context = context) }
    )
    DeleteMapSheet(mapName = mapNameOriginal.value, state = deleteMapSheetState) {
        deleteChosenMap(context, mapsFile)
    }
    recompose.value
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun PickMapFileButton(pickMapSheetState: ModalBottomSheetState, pickMapSheetScrollState: ScrollState) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    PFToolButton(
        title = context.getString(R.string.manageMapsPickMap),
        painter = painterResource(id = R.drawable.map),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        recompose.value = !recompose.value
        scope.launch {
            keyboardController?.hide()
            pickMapSheetScrollState.scrollTo(0)
            pickMapSheetState.show()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun MapActions(mapsFile: DocumentFileCompat, deleteMapSheetState: ModalBottomSheetState) {
    if (mapPath.value != "") {
        val context = LocalContext.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val scope = rememberCoroutineScope()
        val isImported = mapPath.value.startsWith(mapsDir)
        val isExported = mapPath.value.startsWith(mapsExportDir)
        val isZip = mapPath.value.lowercase().endsWith(".zip")
        PFToolColumnRounded(title = context.getString(R.string.manageMapsMapName)) {
            PFToolTextField(
                value = mapNameEdit.value,
                placeholder = { Text(mapNameOriginal.value) },
                onValueChange = { mapNameEdit.value = it },
                singleLine = true
            )
            AnimatedVisibility(visible = isImported && getMapNameEdit() != mapNameOriginal.value) {
                PFToolButton(
                    title = context.getString(R.string.manageMapsRename),
                    painter = painterResource(id = R.drawable.edit),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    renameChosenMap(context, mapsFile)
                }
            }
        }
        AnimatedVisibility(visible = !isImported) {
            PFToolButton(
                title = context.getString(R.string.manageMapsImport),
                painter = painterResource(id = R.drawable.download),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                importChosenMap(context, mapsFile)
            }
        }
        AnimatedVisibility(visible = isImported) {
            PFToolButton(
                title = context.getString(R.string.manageMapsExport),
                description = context.getString(R.string.manageMapsExportDescription),
                painter = painterResource(id = R.drawable.share)
            ) {
                exportChosenMap(context, mapsFile)
            }
        }
        AnimatedVisibility(visible = isZip) {
            PFToolButton(
                title = context.getString(R.string.manageMapsShare),
                painter = painterResource(id = R.drawable.share)
            ) {
                FileUtil.shareFile(mapPath.value, "application/zip", context)
            }
        }
        AnimatedVisibility(visible = isImported || isExported) {
            PFToolButton(
                title = context.getString(R.string.manageMapsDelete),
                painter = painterResource(id = R.drawable.trash),
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ) {
                scope.launch {
                    keyboardController?.hide()
                    deleteMapSheetState.show()
                }
            }
        }
    }
}

private fun getMap(path: String? = null, mapFile: DocumentFileCompat? = null, context: Context) {
    if (path != null) {
        val file = File(path)
        var mapName = file.name
        if (!file.isDirectory) mapName = file.nameWithoutExtension
        if (file.exists()) {
            mapPath.value = file.absolutePath
            mapNameEdit.value = mapName
            mapNameOriginal.value = mapNameEdit.value
            mapIsUri.value = false
        } else {
            topToastManager.showToast(context.getString(R.string.warning_fileDoesntExist), iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
        }
    } else if (mapFile != null) {
        var mapName = mapFile.name
        if (!mapFile.isDirectory()) mapName = FileUtil.removeExtension(mapName)
        if (mapFile.exists()) {
            mapPath.value = "$mapsDir/$mapName"
            mapNameEdit.value = mapName
            mapNameOriginal.value = mapNameEdit.value
            mapIsUri.value = true
        } else {
            topToastManager.showToast(context.getString(R.string.warning_fileDoesntExist), iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
        }
    } else {
        mapPath.value = ""
        mapNameEdit.value = ""
        mapNameOriginal.value = ""
    }
}

private fun renameChosenMap(context: Context, mapsFile: DocumentFileCompat) {
    val outputFile = mapsFile.findFile(getMapNameEdit())
    if (outputFile != null && outputFile.exists()) {
        topToastManager.showToast(context.getString(R.string.warning_mapAlreadyExists), iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
    } else {
        mapsFile.findFile(mapNameOriginal.value)?.renameTo(getMapNameEdit())
        getMap(mapFile = mapsFile.findFile(getMapNameEdit()), context = context)
        topToastManager.showToast(context.getString(R.string.info_done), iconDrawableId = R.drawable.check, iconTintColorType = TopToastColorType.PRIMARY)
    }
}

private fun importChosenMap(context: Context, mapsFile: DocumentFileCompat) {
    var outputFile = mapsFile.findFile(getMapNameEdit())
    if (outputFile != null && outputFile.exists()) {
        topToastManager.showToast(context.getString(R.string.warning_mapAlreadyExists), iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
    } else {
        outputFile = mapsFile.createDirectory(getMapNameEdit())
        if (outputFile != null) ZipUtil.unzipMap(mapPath.value, outputFile, context)
        getMap(mapFile = outputFile, context = context)
        topToastManager.showToast(context.getString(R.string.info_done), iconDrawableId = R.drawable.check, iconTintColorType = TopToastColorType.PRIMARY)
    }
}

private fun exportChosenMap(context: Context, mapsFile: DocumentFileCompat) {
    val outputFile = File("${mapsExportDir}/${getMapNameEdit()}.zip")
    if (!outputFile.parentFile?.isDirectory!!) outputFile.parentFile?.mkdirs()
    if (outputFile.exists()) {
        topToastManager.showToast(context.getString(R.string.warning_mapAlreadyExists), iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
    } else {
        ZipUtil.zipMap(folder = mapsFile.findFile(mapNameOriginal.value)!!, zipPath = outputFile.absolutePath, context)
        getMap(outputFile.absolutePath, context = context)
        topToastManager.showToast(context.getString(R.string.info_exportedMap), iconDrawableId = R.drawable.share, iconTintColorType = TopToastColorType.PRIMARY)
    }
}

private fun deleteChosenMap(context: Context, mapsFile: DocumentFileCompat) {
    if (mapIsUri.value) mapsFile.findFile(mapNameOriginal.value)?.delete()
    else File(mapPath.value).delete()
    getMap(context = context)
    topToastManager.showToast(context.getString(R.string.info_done), iconDrawableId = R.drawable.check, iconTintColorType = TopToastColorType.PRIMARY)
}

private fun getMapNameEdit(): String {
    return mapNameEdit.value.ifBlank { mapNameOriginal.value }
}