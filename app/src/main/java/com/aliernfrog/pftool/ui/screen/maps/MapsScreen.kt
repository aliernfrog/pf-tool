package com.aliernfrog.pftool.ui.screen.maps

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Share
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
import com.aliernfrog.pftool.ui.component.AppTopBar
import com.aliernfrog.pftool.ui.component.FadeVisibility
import com.aliernfrog.pftool.ui.component.PickMapButton
import com.aliernfrog.pftool.ui.component.TextField
import com.aliernfrog.pftool.ui.component.VerticalSegmentedButtons
import com.aliernfrog.pftool.ui.component.form.ButtonRow
import com.aliernfrog.pftool.ui.dialog.DeleteConfirmationDialog
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import com.aliernfrog.pftool.util.staticutil.FileUtil
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(
    mapsViewModel: MapsViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(mapsViewModel.chosenMap) {
        if (mapsViewModel.chosenMap == null) mapsViewModel.mapListShown = true
    }

    BackHandler(mapsViewModel.chosenMap != null) {
        mapsViewModel.chooseMap(null)
    }

    AppScaffold(
        topBar = { AppTopBar(
            title = stringResource(R.string.maps),
            scrollBehavior = it
        ) },
        topAppBarState = mapsViewModel.topAppBarState
    ) {
        Column(Modifier.fillMaxSize().verticalScroll(mapsViewModel.scrollState)) {
            PickMapButton(
                chosenMap = mapsViewModel.chosenMap,
                showMapThumbnail = mapsViewModel.prefs.showChosenMapThumbnail
            ) {
                mapsViewModel.mapListShown = true
            }
            MapActions()
        }
    }

    mapsViewModel.pendingMapDelete?.let {
        DeleteConfirmationDialog(
            name = it.name,
            onDismissRequest = { mapsViewModel.pendingMapDelete = null },
            onConfirmDelete = {
                scope.launch {
                    mapsViewModel.deleteMap(it)
                    mapsViewModel.pendingMapDelete = null
                }
            }
        )
    }
}

@Composable
private fun MapActions(
    mapsViewModel: MapsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isImported = mapsViewModel.chosenMap?.importedState == MapImportedState.IMPORTED
    val isExported = mapsViewModel.chosenMap?.importedState == MapImportedState.EXPORTED
    val isZip = mapsViewModel.chosenMap?.isZip == true
    val mapNameUpdated = mapsViewModel.resolveMapNameInput() != mapsViewModel.chosenMap?.name
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
    VerticalSegmentedButtons(
        {
            FadeVisibility(visible = !isImported) {
                ButtonRow(
                    title = stringResource(R.string.maps_import),
                    painter = rememberVectorPainter(Icons.Rounded.Download),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    scope.launch { mapsViewModel.importChosenMap(context) }
                }
            }
        },
        {
            FadeVisibility(visible = isImported) {
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
            FadeVisibility(visible = isZip) {
                ButtonRow(
                    title = stringResource(R.string.maps_share),
                    painter = rememberVectorPainter(Icons.Rounded.Share),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    val map = mapsViewModel.chosenMap
                    val file = map?.file ?: map?.documentFile
                    if (isZip && file != null) scope.launch {
                        FileUtil.shareFile(file, context)
                    }
                }
            }
        },
        {
            FadeVisibility(visible = (isImported || isExported)) {
                ButtonRow(
                    title = stringResource(R.string.maps_delete),
                    painter = rememberVectorPainter(Icons.Rounded.Delete),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.error
                ) {
                    mapsViewModel.pendingMapDelete = mapsViewModel.chosenMap
                }
            }
        },
        modifier = Modifier.padding(8.dp)
    )
}