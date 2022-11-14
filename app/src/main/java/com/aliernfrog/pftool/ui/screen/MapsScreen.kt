package com.aliernfrog.pftool.ui.screen

import androidx.compose.animation.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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
import com.aliernfrog.toptoast.TopToastManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapsScreen(navController: NavController, topToastManager: TopToastManager, mapsState: MapsState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { mapsState.getMapsFile(context); mapsState.getImportedMaps(); mapsState.getExportedMaps() }
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
        onFilePick = { mapsState.getMap(file = it, context = context) },
        onDocumentFilePick = { mapsState.getMap(documentFile = it, context = context) }
    )
    DeleteMapSheet(mapName = mapsState.lastMapName.value, sheetState = deleteMapSheetState) {
        scope.launch { mapsState.deleteChosenMap(context) }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PickMapFileButton(pickMapSheetState: ModalBottomSheetState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mapChosen = mapsState.chosenMap.value != null
    val isImported = mapsState.chosenMap.value?.filePath?.startsWith(mapsState.mapsDir) ?: false
    val isExported = mapsState.chosenMap.value?.filePath?.startsWith(mapsState.mapsExportDir) ?: false
    val isZip = mapsState.chosenMap.value?.filePath?.lowercase()?.endsWith(".zip") ?: false
    MapActionVisibility(visible = mapChosen) {
        PFToolColumnRounded(title = context.getString(R.string.manageMapsMapName)) {
            PFToolTextField(
                value = mapsState.mapNameEdit.value,
                placeholder = { Text(mapsState.chosenMap.value!!.mapName) },
                onValueChange = { mapsState.mapNameEdit.value = it },
                singleLine = true
            )
            MapActionVisibility(visible = isImported && mapsState.getMapNameEdit() != mapsState.chosenMap.value!!.mapName) {
                PFToolButton(
                    title = context.getString(R.string.manageMapsRename),
                    painter = painterResource(id = R.drawable.edit),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    scope.launch { mapsState.renameChosenMap(context) }
                }
            }
        }
    }
    MapActionVisibility(visible = mapChosen && !isImported) {
        PFToolButton(
            title = context.getString(R.string.manageMapsImport),
            painter = painterResource(id = R.drawable.download),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            scope.launch { mapsState.importChosenMap(context) }
        }
    }
    MapActionVisibility(visible = mapChosen && isImported) {
        PFToolButton(
            title = context.getString(R.string.manageMapsExport),
            description = context.getString(R.string.manageMapsExportDescription),
            painter = painterResource(id = R.drawable.share)
        ) {
            scope.launch { mapsState.exportChosenMap(context) }
        }
    }
    MapActionVisibility(visible = mapChosen && isZip) {
        PFToolButton(
            title = context.getString(R.string.manageMapsShare),
            painter = painterResource(id = R.drawable.share)
        ) {
            FileUtil.shareFile(mapsState.chosenMap.value!!.filePath, "application/zip", context)
        }
    }
    MapActionVisibility(visible = mapChosen && (isImported || isExported)) {
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

@Composable
private fun MapActionVisibility(visible: Boolean, content: @Composable AnimatedVisibilityScope.() -> Unit) {
    return AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        content = content
    )
}