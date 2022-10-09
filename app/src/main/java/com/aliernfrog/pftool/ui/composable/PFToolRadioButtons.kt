package com.aliernfrog.pftool.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PFToolRadioButtons(
    options: List<String>,
    initialIndex: Int = 0,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    onSelect: (Int) -> Unit
) {
    val (selectedIndex, onOptionSelect) = remember { mutableStateOf(initialIndex) }
    options.forEachIndexed { index, option ->
        val selected = selectedIndex == index
        val onSelected = { onOptionSelect(index); onSelect(index) }
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().clickable { onSelected() }
        ) {
            RadioButton(colors = RadioButtonDefaults.colors(selectedColor = selectedColor, unselectedColor = contentColor.copy(0.5f), disabledSelectedColor = contentColor.copy(0.7f), disabledUnselectedColor = contentColor.copy(0.5f)),
                selected = selected,
                onClick = { onSelected() }
            )
            Text(text = option, fontWeight = FontWeight.Bold, color = contentColor)
        }
    }
}