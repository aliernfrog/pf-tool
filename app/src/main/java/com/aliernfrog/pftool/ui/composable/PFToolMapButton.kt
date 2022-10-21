package com.aliernfrog.pftool.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
    var thumbnailLoaded by remember { mutableStateOf(false) }
    Button(
        onClick = onClick,
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        shape = PFToolComposableShape,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        contentPadding = PaddingValues(8.dp)
    ) {
        AsyncImage(
            model = map.thumbnailPainterModel,
            contentDescription = null,
            modifier = Modifier.padding(end = 4.dp).size(40.dp).padding(4.dp),
            placeholder = painterResource(R.drawable.map),
            error = painterResource(R.drawable.map),
            fallback = painterResource(R.drawable.map),
            onSuccess = { thumbnailLoaded = true },
            colorFilter = if (!thumbnailLoaded) ColorFilter.tint(contentColor) else null
        )
        Column(Modifier.fillMaxWidth()) {
            Text(map.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(FileUtil.lastModifiedFromLong(map.lastModified, context), Modifier.alpha(0.8f), fontSize = 12.sp)
        }
    }
}