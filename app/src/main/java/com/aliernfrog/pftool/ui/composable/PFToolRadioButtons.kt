package com.aliernfrog.pftool.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PFToolRadioButtons(options: List<String>, columnColor: Color = MaterialTheme.colors.secondary, selectedIndex: Int = 0, onSelect: (String) -> Unit) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(options[selectedIndex]) }
    PFToolColumnRounded(color = columnColor) {
        options.forEach { option ->
            val isSelected = selectedOption === option
            val onSelected = {
                onOptionSelected(option)
                onSelect(option)
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelected() }) {
                RadioButton(colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.primary),
                    selected = isSelected,
                    onClick = { onSelected() }
                )
                Text(text = option, fontWeight = FontWeight.Bold)
            }
        }
    }
}