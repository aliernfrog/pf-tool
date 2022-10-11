package com.aliernfrog.pftool.ui.screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolTextField
import com.aliernfrog.pftool.ui.sheet.DeleteMapSheet
import com.aliernfrog.pftool.ui.sheet.PickMapSheet
import com.aliernfrog.pftool.ui.state.MapsState
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

private lateinit var mapsDir: String
private lateinit var mapsExportDir: String

private lateinit var scope: CoroutineScope
private lateinit var topToastManager: TopToastManager

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapsScreen(navController: NavController, toastManager: TopToastManager, mapsState: MapsState) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { mapsState.getMapsFile(context); mapsState.getImportedMaps(); mapsState.getExportedMaps() }
    scope = rememberCoroutineScope()
    mapsDir = mapsState.mapsDir
    mapsExportDir = mapsState.mapsExportDir
    topToastManager = toastManager
    val pickMapSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val deleteMapSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    PFToolBaseScaffold(title = context.getString(R.string.manageMaps), navController = navController) {
        PickMapFileButton(pickMapSheetState)
        MapActions(mapsState, deleteMapSheetState)
    }
    PickMapSheet(
        mapsState = mapsState,
        topToastManager = topToastManager,
        sheetState = pickMapSheetState,
        onPathPick = { getMap(it, context = context, mapsState = mapsState) },
        onMapFilePick = { getMap(mapFile = it, mapsState = mapsState, context = context) }
    )
    DeleteMapSheet(mapName = mapsState.lastMapName.value, sheetState = deleteMapSheetState) {
        deleteChosenMap(context, mapsState)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PickMapFileButton(pickMapSheetState: ModalBottomSheetState) {
    val context = LocalContext.current
    PFToolButton(
        title = context.getString(R.string.manageMapsPickMap),
        painter = painterResource(id = R.drawable.map),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        scope.launch { pickMapSheetState.show() }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MapActions(mapsState: MapsState, deleteMapSheetState: ModalBottomSheetState) {
    if (mapPath.value != "") {
        val context = LocalContext.current
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
                    renameChosenMap(context, mapsState)
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
                importChosenMap(context, mapsState)
            }
        }
        AnimatedVisibility(visible = isImported) {
            PFToolButton(
                title = context.getString(R.string.manageMapsExport),
                description = context.getString(R.string.manageMapsExportDescription),
                painter = painterResource(id = R.drawable.share)
            ) {
                exportChosenMap(context, mapsState)
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
                scope.launch { deleteMapSheetState.show() }
            }
        }
    }
}

private fun getMap(path: String? = null, mapFile: DocumentFileCompat? = null, mapsState: MapsState, context: Context) {
    if (path != null) {
        val file = File(path)
        var mapName = file.name
        if (!file.isDirectory) mapName = file.nameWithoutExtension
        if (file.exists()) {
            mapPath.value = file.absolutePath
            mapNameEdit.value = mapName
            mapNameOriginal.value = mapNameEdit.value
            mapsState.lastMapName.value = mapName
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
            mapsState.lastMapName.value = mapName
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

private fun renameChosenMap(context: Context, mapsState: MapsState) {
    val outputFile = mapsState.mapsFile.findFile(getMapNameEdit())
    if (outputFile != null && outputFile.exists()) {
        topToastManager.showToast(context.getString(R.string.warning_mapAlreadyExists), iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
    } else {
        mapsState.mapsFile.findFile(mapNameOriginal.value)?.renameTo(getMapNameEdit())
        getMap(mapFile = mapsState.mapsFile.findFile(getMapNameEdit()), mapsState = mapsState, context = context)
        topToastManager.showToast(context.getString(R.string.info_done), iconDrawableId = R.drawable.check, iconTintColorType = TopToastColorType.PRIMARY)
        updateMaps(mapsState, exported = false)
    }
}

private fun importChosenMap(context: Context, mapsState: MapsState) {
    var outputFile = mapsState.mapsFile.findFile(getMapNameEdit())
    if (outputFile != null && outputFile.exists()) {
        topToastManager.showToast(context.getString(R.string.warning_mapAlreadyExists), iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
    } else {
        outputFile = mapsState.mapsFile.createDirectory(getMapNameEdit())
        if (outputFile != null) ZipUtil.unzipMap(mapPath.value, outputFile, context)
        getMap(mapFile = outputFile, mapsState = mapsState, context = context)
        topToastManager.showToast(context.getString(R.string.info_done), iconDrawableId = R.drawable.check, iconTintColorType = TopToastColorType.PRIMARY)
        updateMaps(mapsState, exported = false)
    }
}

private fun exportChosenMap(context: Context, mapsState: MapsState) {
    val outputFile = File("${mapsExportDir}/${getMapNameEdit()}.zip")
    if (!outputFile.parentFile?.isDirectory!!) outputFile.parentFile?.mkdirs()
    if (outputFile.exists()) {
        topToastManager.showToast(context.getString(R.string.warning_mapAlreadyExists), iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
    } else {
        ZipUtil.zipMap(folder = mapsState.mapsFile.findFile(mapNameOriginal.value)!!, zipPath = outputFile.absolutePath, context)
        getMap(outputFile.absolutePath, mapsState = mapsState, context = context)
        topToastManager.showToast(context.getString(R.string.info_exportedMap), iconDrawableId = R.drawable.share, iconTintColorType = TopToastColorType.PRIMARY)
        updateMaps(mapsState, imported = false)
    }
}

private fun deleteChosenMap(context: Context, mapsState: MapsState) {
    if (mapIsUri.value) mapsState.mapsFile.findFile(mapNameOriginal.value)?.delete()
    else File(mapPath.value).delete()
    getMap(mapsState = mapsState, context = context)
    topToastManager.showToast(context.getString(R.string.info_done), iconDrawableId = R.drawable.check, iconTintColorType = TopToastColorType.PRIMARY)
    updateMaps(mapsState, imported = mapIsUri.value, exported = !mapIsUri.value)
}

private fun getMapNameEdit(): String {
    return mapNameEdit.value.ifBlank { mapNameOriginal.value }
}

private fun updateMaps(mapsState: MapsState, imported: Boolean = true, exported: Boolean = true) {
    scope.launch {
        if (imported) mapsState.getImportedMaps()
        if (exported) mapsState.getExportedMaps()
    }
}