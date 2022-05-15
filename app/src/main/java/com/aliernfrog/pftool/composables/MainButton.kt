package com.aliernfrog.pftool.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainButton(title: String, description: String, painter: Painter, backgroundColor: Color, contentColor: Color, onClick: () -> Unit) {
    Button(modifier = Modifier
        .padding(all = 8.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(20),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor, contentColor = contentColor),
        onClick = onClick,
        contentPadding = PaddingValues(all = 8.dp),
        content = {
            Image(painter, title)
            Column(Modifier.fillMaxWidth().padding(start = 8.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(description, Modifier.alpha(0.8f), fontSize = 12.sp)
            }
        })
}