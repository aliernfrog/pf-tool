package io.github.aliernfrog.pftool_shared.ui.screen.maps

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.pftool_shared.data.MapAction
import io.github.aliernfrog.pftool_shared.impl.DefaultMapActionArguments
import io.github.aliernfrog.pftool_shared.impl.IMapFile
import io.github.aliernfrog.pftool_shared.ui.component.maps.GridMapItem
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppSmallTopBar
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.FadeVisibility
import io.github.aliernfrog.shared.ui.component.FilledIconButtonWithTooltip
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.ui.theme.AppComponentShape
import io.github.aliernfrog.shared.util.extension.clickableWithColor
import io.github.aliernfrog.shared.util.sharedStringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapDetailsScreen(
    map: IMapFile,
    mapActions: List<MapAction>,
    showMapThumbnail: Boolean,
    showMapNameFieldGuide: Boolean,
    settingsButton: (@Composable () -> Unit)?,
    onDismissMapNameFieldGuide: () -> Unit,
    onViewThumbnailRequest: () -> Unit,
    onNavigateBackRequest: (() -> Unit)?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val renameAction = remember {
        mapActions.find { it.id == MapAction.RENAME_ID }
    }
    val duplicateAction = remember {
        mapActions.find { it.id == MapAction.DUPLICATE_ID }
    }

    val mapNameEdit = rememberSaveable {
        mutableStateOf(map.name.replace("\n",""))
    }

    val isSameName = mapNameEdit.value.let {
        it.isBlank() || it == map.name
    }

    AppScaffold(
        topBar = { scrollBehavior ->
            AppSmallTopBar(
                title = map.name,
                scrollBehavior = scrollBehavior,
                onNavigationClick = onNavigateBackRequest,
                actions = {
                    settingsButton?.invoke()
                }
            )
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            VerticalSegmentor({
                MapCard(
                    chosenMap = map,
                    showMapThumbnail = showMapThumbnail,
                    onViewThumbnailRequest = onViewThumbnailRequest,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .heightIn(min = 150.dp, max = 150.dp)
                )
            }, {
                TextField(
                    value = mapNameEdit.value,
                    onValueChange = {
                        mapNameEdit.value = it.replace("\n", "")
                    },
                    label = {
                        Text(sharedStringResource(PFToolSharedString.MapsMapName))
                    },
                    placeholder = { Text(map.name) },
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

            FadeVisibility(visible = showMapNameFieldGuide) {
                OutlinedCard(
                    onClick = onDismissMapNameFieldGuide,
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
                                text = sharedStringResource(PFToolSharedString.MapsMapNameGuide),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = sharedStringResource(PFToolSharedString.ActionTapToDismiss),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.alpha(0.7f)
                            )
                        }
                    }
                }
            }

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
                Crossfade(
                    targetState = duplicateAction?.availableFor(map) == true && !isSameName
                ) { available ->
                    OutlinedButton(
                        onClick = { scope.launch {
                            duplicateAction?.execute(context, listOf(map), DefaultMapActionArguments(
                                mapName = mapNameEdit.value
                            ))
                        } },
                        shapes = ButtonDefaults.shapes(),
                        enabled = available
                    ) {
                        ButtonIcon(rememberVectorPainter(Icons.Default.FileCopy))
                        Text(sharedStringResource(PFToolSharedString.MapsDuplicate))
                    }
                }

                Crossfade(
                    targetState = renameAction?.availableFor(map) == true && !isSameName
                ) { available ->
                    Button(
                        onClick = { scope.launch {
                            renameAction?.execute(context, listOf(map), DefaultMapActionArguments(
                                mapName = mapNameEdit.value
                            ))
                        } },
                        shapes = ButtonDefaults.shapes(),
                        enabled = available
                    ) {
                        ButtonIcon(rememberVectorPainter(Icons.Default.Edit))
                        Text(sharedStringResource(PFToolSharedString.MapsRename))
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).alpha(0.7f),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            val actions: List<@Composable () -> Unit> = mapActions.filter { action ->
                action != renameAction && action != duplicateAction
            }.map { action -> {
                FadeVisibility(visible = action.availableFor(map)) {
                    ExpressiveButtonRow(
                        title = sharedStringResource(action.longLabel),
                        description = action.description?.let {
                            sharedStringResource(it)
                        },
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
                        action.execute(context, listOf(map), DefaultMapActionArguments(mapName = mapNameEdit.value))
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
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MapCard(
    chosenMap: IMapFile,
    showMapThumbnail: Boolean,
    onViewThumbnailRequest: () -> Unit,
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
        if (chosenMap.thumbnailModel != null && showMapThumbnail) FilledIconButtonWithTooltip(
            icon = rememberVectorPainter(Icons.Default.Fullscreen),
            contentDescription = sharedStringResource(PFToolSharedString.MapsThumbnail),
            onClick = onViewThumbnailRequest,
            modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
            )
        )
    }
}