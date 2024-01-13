package com.aliernfrog.pftool.ui.screen.maps

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
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
import com.aliernfrog.pftool.enum.MapAction
import com.aliernfrog.pftool.enum.MapImportedState
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.component.AppTopBar
import com.aliernfrog.pftool.ui.component.FadeVisibility
import com.aliernfrog.pftool.ui.component.PickMapButton
import com.aliernfrog.pftool.ui.component.TextField
import com.aliernfrog.pftool.ui.component.VerticalSegmentedButtons
import com.aliernfrog.pftool.ui.component.form.ButtonRow
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(
    mapsViewModel: MapsViewModel = koinViewModel()
) {
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
            Actions()
        }
    }
}

@Composable
private fun Actions(
    mapsViewModel: MapsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val chosenMap = mapsViewModel.chosenMap ?: return
    val canBeRenamed = chosenMap.importedState != MapImportedState.NONE
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
        doneIconShown = canBeRenamed && mapNameUpdated,
        onDone = { scope.launch { chosenMap.rename() } }
    )
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).alpha(0.7f),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    )

    val actions: List<@Composable () -> Unit> = MapAction.entries.map { action -> {
        FadeVisibility(visible = action.availableFor(chosenMap)) {
            ButtonRow(
                title = stringResource(action.longLabelId),
                description = action.descriptionId?.let { stringResource(it) },
                painter = rememberVectorPainter(action.icon),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = if (action.destructive) MaterialTheme.colorScheme.error
                else contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) { scope.launch {
                action.execute(context = context, chosenMap)
            } }
        }
    } }

    VerticalSegmentedButtons(
        *actions.toTypedArray(),
        modifier = Modifier.padding(8.dp)
    )
}