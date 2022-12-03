package com.aliernfrog.pftool.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.state.MapsState
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolTextField
import com.aliernfrog.pftool.util.FileUtil
import kotlinx.coroutines.launch

@Composable
fun MapsScreen(mapsState: MapsState) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { mapsState.getMapsFile(context); mapsState.getImportedMaps(); mapsState.getExportedMaps() }
    Column(Modifier.fillMaxSize().verticalScroll(mapsState.scrollState)) {
        PickMapFileButton(mapsState)
        MapActions(mapsState)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PickMapFileButton(mapsState: MapsState) {
    val scope = rememberCoroutineScope()
    PFToolButton(
        title = stringResource(R.string.manageMapsPickMap),
        painter = rememberVectorPainter(Icons.Default.PinDrop),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        scope.launch { mapsState.pickMapSheetState.show() }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MapActions(mapsState: MapsState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mapChosen = mapsState.chosenMap.value != null
    val isImported = mapsState.chosenMap.value?.filePath?.startsWith(mapsState.mapsDir) ?: false
    val isExported = mapsState.chosenMap.value?.filePath?.startsWith(mapsState.mapsExportDir) ?: false
    val isZip = mapsState.chosenMap.value?.filePath?.lowercase()?.endsWith(".zip") ?: false
    MapActionVisibility(visible = mapChosen) {
        PFToolColumnRounded(title = stringResource(R.string.manageMapsMapName)) {
            PFToolTextField(
                value = mapsState.mapNameEdit.value,
                placeholder = { Text(mapsState.chosenMap.value!!.mapName) },
                onValueChange = { mapsState.mapNameEdit.value = it },
                singleLine = true
            )
            MapActionVisibility(visible = isImported && mapsState.getMapNameEdit() != mapsState.chosenMap.value!!.mapName) {
                PFToolButton(
                    title = stringResource(R.string.manageMapsRename),
                    painter = rememberVectorPainter(Icons.Default.Edit),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    scope.launch { mapsState.renameChosenMap(context) }
                }
            }
        }
    }
    MapActionVisibility(visible = mapChosen && !isImported) {
        PFToolButton(
            title = stringResource(R.string.manageMapsImport),
            painter = rememberVectorPainter(Icons.Default.Download),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            scope.launch { mapsState.importChosenMap(context) }
        }
    }
    MapActionVisibility(visible = mapChosen && isImported) {
        PFToolButton(
            title = stringResource(R.string.manageMapsExport),
            description = stringResource(R.string.manageMapsExportDescription),
            painter = rememberVectorPainter(Icons.Default.Upload)
        ) {
            scope.launch { mapsState.exportChosenMap(context) }
        }
    }
    MapActionVisibility(visible = mapChosen && isZip) {
        PFToolButton(
            title = stringResource(R.string.manageMapsShare),
            painter = rememberVectorPainter(Icons.Outlined.IosShare)
        ) {
            FileUtil.shareFile(mapsState.chosenMap.value!!.filePath, "application/zip", context)
        }
    }
    MapActionVisibility(visible = mapChosen && (isImported || isExported)) {
        PFToolButton(
            title = stringResource(R.string.manageMapsDelete),
            painter = rememberVectorPainter(Icons.Default.Delete),
            containerColor = MaterialTheme.colorScheme.error
        ) {
            scope.launch { mapsState.deleteMapSheetState.show() }
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