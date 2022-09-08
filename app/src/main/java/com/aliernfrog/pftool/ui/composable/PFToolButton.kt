package com.aliernfrog.pftool.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PFToolButton(
    title: String,
    description: String? = null,
    painter: Painter? = null,
    backgroundColor: Color = MaterialTheme.colors.secondary,
    contentColor: Color = MaterialTheme.colors.onBackground,
    painterTintColor: Color? = MaterialTheme.colors.onBackground,
    painterBackgroundColor: Color? = null,
    enabled: Boolean = true,
    onClick: () -> Unit) {
    Button(modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor, contentColor = contentColor),
        onClick = onClick,
        enabled = enabled,
        contentPadding = PaddingValues(all = 8.dp)
    ) {
        if (painter != null) {
            val modifier = if (painterBackgroundColor != null) Modifier.padding(end = 8.dp).size(40.dp).clip(CircleShape).background(painterBackgroundColor).padding(6.dp)
            else Modifier.padding(end = 4.dp).size(40.dp).padding(4.dp)
            Image(painter, title, modifier, colorFilter = if (painterTintColor != null) ColorFilter.tint(painterTintColor) else null)
        }
        Column(Modifier.fillMaxWidth()) {
            Text(title, fontWeight = FontWeight.Bold)
            if (description != null) Text(description, Modifier.alpha(0.8f), fontSize = 12.sp)
        }
    }
}