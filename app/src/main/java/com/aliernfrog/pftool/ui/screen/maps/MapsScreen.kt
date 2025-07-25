package com.aliernfrog.pftool.ui.screen.maps

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SheetState
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.enum.MapAction
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.ui.component.ButtonIcon
import com.aliernfrog.pftool.ui.component.FadeVisibility
import com.aliernfrog.pftool.ui.component.FakeModalBottomSheetScaffold
import com.aliernfrog.pftool.ui.component.VerticalSegmentor
import com.aliernfrog.pftool.ui.component.expressive.ExpressiveButtonRow
import com.aliernfrog.pftool.ui.component.expressive.ExpressiveRowIcon
import com.aliernfrog.pftool.ui.component.maps.GridMapItem
import com.aliernfrog.pftool.ui.theme.AppComponentShape
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import com.aliernfrog.pftool.util.extension.clickableWithColor
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MapsScreen(
    mapsViewModel: MapsViewModel = koinViewModel(),
    onNavigateSettingsRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = mapsViewModel.mapSheetState
    )

    fun chooseMap(map: MapFile) {
        scope.launch {
            mapsViewModel.viewMap(map)
        }
    }

    FakeModalBottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            mapsViewModel.chosenMap?.let {
                MapSheetContent(
                    chosenMap = it,
                    sheetState = scaffoldState.bottomSheetState
                )
            }
        }
    ) {
        MapsListScreen(
            title = stringResource(R.string.maps),
            onBackClick = null,
            onNavigateSettingsRequest = onNavigateSettingsRequest,
            smallFloatingActionButton = {
                mapsViewModel.chosenMap.let {
                    AnimatedVisibility(it != null) {
                        SmallExtendedFloatingActionButton(
                            onClick = { scope.launch {
                                mapsViewModel.mapSheetState.expand()
                            } }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp).size(24.dp)
                            )
                            Text(it?.name ?: "")
                        }
                    }
                }
            },
            onMapPick = {
                chooseMap(it)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MapSheetContent(
    chosenMap: MapFile,
    sheetState: SheetState,
    mapsViewModel: MapsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    BackHandler {
        scope.launch {
            sheetState.hide()
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        VerticalSegmentor({
            MapCard(
                chosenMap = chosenMap,
                showMapThumbnail = mapsViewModel.prefs.showChosenMapThumbnail.value,
                onViewThumbnailRequest = {
                    mapsViewModel.openMapThumbnailViewer(chosenMap)
                },
                onCloseRequest = {
                    scope.launch {
                        sheetState.hide()
                        mapsViewModel.chosenMap = null
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .heightIn(min = 150.dp, max = 150.dp)
            )
        }, {
            TextField(
                value = mapsViewModel.mapNameEdit,
                onValueChange = { mapsViewModel.mapNameEdit = it },
                label = { Text(stringResource(R.string.maps_mapName)) },
                placeholder = { Text(chosenMap.name) },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.TextFields,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }, modifier = Modifier.padding(
            start = 12.dp,
            end = 12.dp,
            bottom = 8.dp
        ))

        FadeVisibility(visible = mapsViewModel.prefs.showMapNameFieldGuide.value) {
            OutlinedCard(
                onClick = { mapsViewModel.prefs.showMapNameFieldGuide.value = false },
                shape = AppComponentShape,
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 4.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                ) {
                    Icon(Icons.Rounded.TipsAndUpdates, contentDescription = null)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.maps_mapName_guide),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = stringResource(R.string.action_tapToDismiss),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
        }

        Crossfade(targetState = MapAction.RENAME.availableFor(chosenMap)) { buttonsEnabled ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 4.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                OutlinedButton(
                    onClick = { scope.launch {
                        MapAction.DUPLICATE.execute(context, chosenMap)
                    } },
                    shapes = ButtonDefaults.shapes(),
                    enabled = buttonsEnabled
                ) {
                    ButtonIcon(rememberVectorPainter(Icons.Default.FileCopy))
                    Text(stringResource(R.string.maps_duplicate))
                }
                Button(
                    onClick = { scope.launch {
                        MapAction.RENAME.execute(context, chosenMap)
                    } },
                    shapes = ButtonDefaults.shapes(),
                    enabled = buttonsEnabled
                ) {
                    ButtonIcon(rememberVectorPainter(Icons.Default.Edit))
                    Text(stringResource(R.string.maps_rename))
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).alpha(0.7f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )

        val actions: List<@Composable () -> Unit> = MapAction.entries.filter { action ->
            action != MapAction.RENAME && action != MapAction.DUPLICATE
        }.map { action -> {
            FadeVisibility(visible = action.availableFor(chosenMap)) {
                ExpressiveButtonRow(
                    title = stringResource(action.longLabel),
                    description = action.description?.let { stringResource(it) },
                    icon = {
                        ExpressiveRowIcon(
                            painter = rememberVectorPainter(action.icon),
                            containerColor = if (action.destructive) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primaryContainer
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = if (action.destructive) MaterialTheme.colorScheme.error
                    else contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) { scope.launch {
                    action.execute(context = context, chosenMap)
                } }
            }
        } }

        VerticalSegmentor(
            *actions.toTypedArray(),
            dynamic = true,
            modifier = Modifier.padding(
                vertical = 8.dp,
                horizontal = 12.dp
            )
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MapCard(
    chosenMap: MapFile,
    showMapThumbnail: Boolean,
    onViewThumbnailRequest: () -> Unit,
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.clickableWithColor(
            color = MaterialTheme.colorScheme.onSurface,
            onClick = onViewThumbnailRequest
        )
    ) {
        GridMapItem(
            map = chosenMap,
            selected = null,
            showMapThumbnail = showMapThumbnail,
            onSelectedChange = {},
            onLongClick = {},
            onClick = onViewThumbnailRequest,
            aspectRatio = null,
            placeholderIcon = if (chosenMap.thumbnailModel != null) Icons.Outlined.Image else Icons.Outlined.HideImage,
            modifier = modifier
        )
        if (chosenMap.thumbnailModel != null && showMapThumbnail) FilledIconButton(
            onClick = onViewThumbnailRequest,
            shapes = IconButtonDefaults.shapes(),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
            ),
            modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = stringResource(R.string.maps_thumbnail)
            )
        }
        FilledIconButton(
            onClick = onCloseRequest,
            shapes = IconButtonDefaults.shapes(),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
            ),
            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.action_close)
            )
        }
    }
}