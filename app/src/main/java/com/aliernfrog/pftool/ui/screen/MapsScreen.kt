package com.aliernfrog.pftool.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PinDrop
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.aliernfrog.pftool.enum.MapImportedState
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.component.FadeVisibility
import com.aliernfrog.pftool.ui.component.FadeVisibilityColumn
import com.aliernfrog.pftool.ui.component.TextField
import com.aliernfrog.pftool.ui.component.VerticalSegmentedButtons
import com.aliernfrog.pftool.ui.component.form.ButtonRow
import com.aliernfrog.pftool.ui.component.form.RoundedButtonRow
import com.aliernfrog.pftool.ui.dialog.DeleteConfirmationDialog
import com.aliernfrog.pftool.ui.sheet.PickMapSheet
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import com.aliernfrog.pftool.util.extension.resolvePath
import com.aliernfrog.pftool.util.staticutil.FileUtil
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(
    mapsViewModel: MapsViewModel = getViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        mapsViewModel.getMapsFile(context)
        mapsViewModel.fetchAllMaps()
    }
    AppScaffold(
        title = stringResource(R.string.maps),
        topAppBarState = mapsViewModel.topAppBarState
    ) {
        Column(Modifier.fillMaxSize().verticalScroll(mapsViewModel.scrollState)) {
            PickMapFileButton { scope.launch {
                mapsViewModel.pickMapSheetState.show()
            }}
            MapActions()
        }
    }

    mapsViewModel.pendingMapDelete?.let {
        DeleteConfirmationDialog(
            name = it,
            onDismissRequest = { mapsViewModel.pendingMapDelete = null },
            onConfirmDelete = {
                scope.launch {
                    mapsViewModel.deleteChosenMap()
                    mapsViewModel.pendingMapDelete = null
                }
            }
        )
    }

    PickMapSheet(
        sheetState = mapsViewModel.pickMapSheetState,
        onMapPick = {
            mapsViewModel.chooseMap(it)
            true
        }
    )
}

@Composable
private fun PickMapFileButton(
    onClick: () -> Unit
) {
    RoundedButtonRow(
        title = stringResource(R.string.maps_pickMap),
        painter = rememberVectorPainter(Icons.Rounded.PinDrop),
        containerColor = MaterialTheme.colorScheme.primary,
        onClick = onClick
    )
}

@Composable
private fun MapActions(
    mapsViewModel: MapsViewModel = getViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mapChosen = mapsViewModel.chosenMap != null
    val isImported = mapsViewModel.chosenMap?.importedState == MapImportedState.IMPORTED
    val isExported = mapsViewModel.chosenMap?.importedState == MapImportedState.EXPORTED
    val isZip = mapsViewModel.chosenMap?.isZip == true
    val mapNameUpdated = mapsViewModel.resolveMapNameInput() != mapsViewModel.chosenMap?.name
    FadeVisibilityColumn(visible = mapChosen) {
        TextField(
            value = mapsViewModel.mapNameEdit,
            onValueChange = { mapsViewModel.mapNameEdit = it },
            label = { Text(stringResource(R.string.maps_mapName)) },
            placeholder = { Text(mapsViewModel.chosenMap?.name ?: "") },
            leadingIcon = rememberVectorPainter(Icons.Rounded.TextFields),
            singleLine = true,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            doneIcon = rememberVectorPainter(Icons.Rounded.Edit),
            doneIconShown = isImported && mapNameUpdated,
            onDone = {
                scope.launch { mapsViewModel.renameChosenMap() }
            }
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).alpha(0.7f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
    VerticalSegmentedButtons(
        {
            FadeVisibility(visible = mapChosen && !isImported) {
                ButtonRow(
                    title = stringResource(R.string.maps_import),
                    painter = rememberVectorPainter(Icons.Rounded.Download),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    scope.launch { mapsViewModel.importChosenMap(context) }
                }
            }
        },
        {
            FadeVisibility(visible = mapChosen && isImported) {
                ButtonRow(
                    title = stringResource(R.string.maps_export),
                    description = stringResource(R.string.maps_export_description),
                    painter = rememberVectorPainter(Icons.Rounded.Upload),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    scope.launch { mapsViewModel.exportChosenMap(context) }
                }
            }
        },
        {
            FadeVisibility(visible = mapChosen && isZip) {
                ButtonRow(
                    title = stringResource(R.string.maps_share),
                    painter = rememberVectorPainter(Icons.Outlined.IosShare),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    val path = mapsViewModel.chosenMap?.resolvePath(mapsViewModel.mapsDir)
                    if (isZip && path != null)
                        FileUtil.shareFile(path, "application/zip", context)
                }
            }
        },
        {
            FadeVisibility(visible = mapChosen && (isImported || isExported)) {
                ButtonRow(
                    title = stringResource(R.string.maps_delete),
                    painter = rememberVectorPainter(Icons.Rounded.Delete),
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    mapsViewModel.pendingMapDelete = mapsViewModel.chosenMap?.name
                }
            }
        },
        modifier = Modifier.padding(8.dp)
    )
}