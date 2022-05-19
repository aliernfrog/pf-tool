package com.aliernfrog.pftool.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PFToolColumnRounded(color: Color = MaterialTheme.colors.secondary, title: String? = null, titleColor: Color = MaterialTheme.colors.onSecondary, content: @Composable ColumnScope.() -> Unit) {
   Column(
       Modifier
           .padding(all = 8.dp)
           .clip(RoundedCornerShape(20.dp))) {
       Column(Modifier.background(color).fillMaxWidth().padding(all = 8.dp)) {
           if (title != null) Text(text = title, fontWeight = FontWeight.Bold, style = LocalTextStyle.current.copy(color = titleColor), modifier = Modifier.padding(horizontal = 8.dp))
           content()
       }
   }
}