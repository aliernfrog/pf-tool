package com.aliernfrog.pftool.ui.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PFToolSegmentedButtons(
    options: List<String>,
    selectedIndex: Int = 0,
    backgroundColor: Color = MaterialTheme.colors.secondary,
    selectedBackgroundColor: Color = MaterialTheme.colors.secondaryVariant,
    contentColor: Color = MaterialTheme.colors.onSecondary,
    selectedContentColor: Color = contentColor,
    onSelect: (String) -> Unit
) {
    val (selectedOption, onOptionSelect) = remember { mutableStateOf(options[selectedIndex]) }
    Crossfade(targetState = selectedOption) {
        Row(Modifier.fillMaxWidth().padding(8.dp).clip(RoundedCornerShape(20.dp)).background(backgroundColor).padding(3.dp)) {
            options.forEach { option ->
                val selected = it == option
                val onClick = { onOptionSelect(option); onSelect(option) }
                Text(
                    text = option,
                    color = if (selected) selectedContentColor else contentColor,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(20.dp))
                        .clickable { onClick() }
                        .background(if (selected) selectedBackgroundColor else backgroundColor)
                        .padding(8.dp)
                )
            }
        }
    }
}