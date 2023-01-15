package com.aliernfrog.pftool.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.state.MapsState
import com.aliernfrog.pftool.ui.component.ButtonRounded
import com.aliernfrog.pftool.ui.component.TextField
import com.aliernfrog.pftool.ui.dialog.DeleteMapDialog
import com.aliernfrog.pftool.util.staticutil.FileUtil
import kotlinx.coroutines.launch

@Composable
fun MapsScreen(mapsState: MapsState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { mapsState.getMapsFile(context); mapsState.getImportedMaps(); mapsState.getExportedMaps() }
    Column(Modifier.fillMaxSize().verticalScroll(mapsState.scrollState)) {
        PickMapFileButton(mapsState)
        MapActions(mapsState)
    }
    if (mapsState.mapDeleteDialogShown.value) DeleteMapDialog(
        mapName = mapsState.lastMapName.value,
        onDismissRequest = { mapsState.mapDeleteDialogShown.value = false },
        onConfirmDelete = {
            scope.launch {
                mapsState.deleteChosenMap()
                mapsState.mapDeleteDialogShown.value = false
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PickMapFileButton(mapsState: MapsState) {
    val scope = rememberCoroutineScope()
    ButtonRounded(
        title = stringResource(R.string.maps_pickMap),
        painter = rememberVectorPainter(Icons.Rounded.PinDrop),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        scope.launch { mapsState.pickMapSheetState.show() }
    }
}

@Composable
private fun MapActions(mapsState: MapsState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mapChosen = mapsState.chosenMap.value != null
    val chosenMapPath = mapsState.getChosenMapPath()
    val isImported = chosenMapPath?.startsWith(mapsState.mapsDir) ?: false
    val isExported = chosenMapPath?.startsWith(mapsState.mapsExportDir) ?: false
    val isZip = chosenMapPath?.lowercase()?.endsWith(".zip") ?: false
    val mapNameUpdated = mapsState.getMapNameEdit() != mapsState.chosenMap.value?.name
    MapActionVisibility(visible = mapChosen) {
        Column {
            TextField(
                value = mapsState.mapNameEdit.value,
                onValueChange = { mapsState.mapNameEdit.value = it },
                label = { Text(stringResource(R.string.maps_mapName)) },
                placeholder = { Text(mapsState.chosenMap.value!!.name) },
                leadingIcon = rememberVectorPainter(Icons.Rounded.TextFields),
                singleLine = true,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                doneIcon = rememberVectorPainter(Icons.Rounded.Edit),
                doneIconShown = isImported && mapNameUpdated,
                onDone = {
                    scope.launch { mapsState.renameChosenMap() }
                }
            )
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).alpha(0.7f),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
    MapActionVisibility(visible = mapChosen && !isImported) {
        ButtonRounded(
            title = stringResource(R.string.maps_import),
            painter = rememberVectorPainter(Icons.Rounded.Download),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            scope.launch { mapsState.importChosenMap(context) }
        }
    }
    MapActionVisibility(visible = mapChosen && isImported) {
        ButtonRounded(
            title = stringResource(R.string.maps_export),
            description = stringResource(R.string.maps_export_description),
            painter = rememberVectorPainter(Icons.Rounded.Upload)
        ) {
            scope.launch { mapsState.exportChosenMap(context) }
        }
    }
    MapActionVisibility(visible = mapChosen && isZip) {
        ButtonRounded(
            title = stringResource(R.string.maps_share),
            painter = rememberVectorPainter(Icons.Outlined.IosShare)
        ) {
            if (chosenMapPath != null) FileUtil.shareFile(chosenMapPath, "application/zip", context)
        }
    }
    MapActionVisibility(visible = mapChosen && (isImported || isExported)) {
        ButtonRounded(
            title = stringResource(R.string.maps_delete),
            painter = rememberVectorPainter(Icons.Rounded.Delete),
            containerColor = MaterialTheme.colorScheme.error
        ) {
            mapsState.mapDeleteDialogShown.value = true
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