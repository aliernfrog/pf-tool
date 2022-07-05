package com.aliernfrog.pftool.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PFToolButtonCentered(title: String, backgroundColor: Color = MaterialTheme.colors.secondary, contentColor: Color = MaterialTheme.colors.onBackground, onClick: () -> Unit) {
    Button(modifier = Modifier
        .padding(all = 8.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor, contentColor = contentColor),
        onClick = onClick,
        contentPadding = PaddingValues(all = 16.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold)
    }
}