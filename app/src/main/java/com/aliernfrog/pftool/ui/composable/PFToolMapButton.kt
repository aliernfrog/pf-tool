package com.aliernfrog.pftool.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aliernfrog.pftool.PFToolComposableShape
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.MapsListItem
import com.aliernfrog.pftool.util.FileUtil

@Composable
fun PFToolMapButton(
    map: MapsListItem,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Box(Modifier.fillMaxWidth().height(IntrinsicSize.Max).padding(8.dp).clip(PFToolComposableShape).background(containerColor).clickable { onClick() }) {
        AsyncImage(
            model = map.thumbnailPainterModel,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            placeholder = ColorPainter(containerColor),
            error = ColorPainter(containerColor),
            fallback = ColorPainter(containerColor),
            contentScale = ContentScale.Crop,
            alpha = 0.5f
        )
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .background(Brush.horizontalGradient(listOf(containerColor,Color.Transparent), startX = 400f))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.map),
                contentDescription = null,
                modifier = Modifier.padding(end = 4.dp).size(40.dp).padding(4.dp),
                colorFilter = ColorFilter.tint(contentColor)
            )
            Column {
                Text(text = map.name, color = contentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = FileUtil.lastModifiedFromLong(map.lastModified, context), modifier = Modifier.alpha(0.9f), color = contentColor, fontSize = 12.sp)
            }
        }
    }
}