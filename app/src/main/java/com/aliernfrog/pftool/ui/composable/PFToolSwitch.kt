package com.aliernfrog.pftool.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.PFToolComposableShape

@Composable
fun PFToolSwitch(
    text: String,
    checked: Boolean,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp).clip(PFToolComposableShape).clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, color = contentColor, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().weight(1f).padding(start = 8.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange, modifier = Modifier.padding(end = 8.dp))
    }
}