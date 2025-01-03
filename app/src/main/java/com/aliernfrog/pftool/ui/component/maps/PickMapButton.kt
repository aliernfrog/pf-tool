package com.aliernfrog.pftool.ui.component.maps

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.ui.component.FadeVisibility
import com.aliernfrog.pftool.ui.theme.AppComponentShape
import com.aliernfrog.pftool.util.extension.clickableWithColor

@Composable
fun PickMapButton(
    chosenMap: MapFile?,
    showMapThumbnail: Boolean,
    onClickThumbnailActions: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(8.dp)
            .clip(AppComponentShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickableWithColor(
                color = MaterialTheme.colorScheme.onPrimary,
                onClick = onClick
            )
    ) {
        FadeVisibility(showMapThumbnail && chosenMap != null) {
            AsyncImage(
                model = chosenMap?.thumbnailModel,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            modifier = Modifier
                .background(Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary,
                        Color.Transparent
                    )
                ))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MapHeader(
                title = chosenMap?.name ?: "",
                description = chosenMap?.details,
                painter = rememberVectorPainter(Icons.Rounded.LocationOn),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(56.dp)
                    .weight(1f)
            )
            IconButton(
                onClick = onClickThumbnailActions
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = stringResource(R.string.maps_thumbnail),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}