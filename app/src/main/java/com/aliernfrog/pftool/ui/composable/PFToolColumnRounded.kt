package com.aliernfrog.pftool.ui.composable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PFToolColumnRounded(color: Color = MaterialTheme.colorScheme.secondaryContainer, title: String? = null, titleColor: Color = MaterialTheme.colorScheme.onSecondaryContainer, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    var modifier = Modifier.fillMaxWidth().padding(8.dp).clip(RoundedCornerShape(30.dp))
    if (onClick != null) modifier = modifier.clickable { onClick() }
    Column(modifier.background(color).animateContentSize().padding(8.dp)) {
        if (title != null) Text(text = title, fontWeight = FontWeight.Bold, color = titleColor, modifier = Modifier.padding(horizontal = 8.dp))
        content()
    }
}