package com.aliernfrog.pftool.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PFMap
import com.aliernfrog.pftool.ui.component.form.DividerRow
import com.aliernfrog.pftool.ui.component.form.FormHeader
import com.aliernfrog.pftool.ui.dialog.DeleteConfirmationDialog
import com.aliernfrog.pftool.ui.theme.AppComponentShape
import com.aliernfrog.pftool.util.extension.clickableWithColor
import com.aliernfrog.pftool.util.extension.getDetails

@Composable
fun MapButton(
    map: PFMap,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = contentColorFor(containerColor),
    showMapThumbnail: Boolean = true,
    showQuickActions: Boolean = true,
    onDeleteRequest: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isRTL = LocalLayoutDirection.current == LayoutDirection.Rtl
    fun invertIfRTL(list: List<Color>): List<Color> {
        return if (isRTL) list.reversed() else list
    }

    var quickActionsExpanded by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    fun resetState() {
        quickActionsExpanded = false
        showDeleteDialog = false
    }

    LaunchedEffect(map) {
        resetState()
        if (map.details.value.isNullOrBlank()) map.getDetails(context)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(8.dp)
            .clip(AppComponentShape)
            .background(containerColor)
            .clickableWithColor(
                color = contentColor,
                onClick = onClick
            )
    ) {
        if (showMapThumbnail) AsyncImage(
            model = map.thumbnailModel,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            placeholder = ColorPainter(containerColor),
            error = ColorPainter(containerColor),
            fallback = ColorPainter(containerColor),
            contentScale = ContentScale.Crop,
            alpha = 0.5f
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            FormHeader(
                title = map.name,
                description = map.details.value ?: "",
                painter = rememberVectorPainter(Icons.Outlined.PinDrop),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Brush.horizontalGradient(
                        invertIfRTL(
                            listOf(containerColor, Color.Transparent)
                        )
                    ))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            if (showQuickActions) Box {
                IconButton(
                    onClick = { quickActionsExpanded = true },
                    modifier = Modifier,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = containerColor.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = stringResource(R.string.maps_pickMap_quickActions),
                        tint = contentColor
                    )
                }

                DropdownMenu(
                    expanded = quickActionsExpanded,
                    onDismissRequest = { quickActionsExpanded = false }
                ) {
                    Text(
                        text = map.name,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        style = MaterialTheme.typography.titleSmall
                    )
                    DividerRow(Modifier.padding(vertical = 4.dp))
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.maps_delete)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.error,
                            leadingIconColor = MaterialTheme.colorScheme.error
                        ),
                        onClick = { showDeleteDialog = true }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) DeleteConfirmationDialog(
        name = map.name,
        onDismissRequest = { showDeleteDialog = false },
        onConfirmDelete = {
            resetState()
            onDeleteRequest()
        }
    )
}