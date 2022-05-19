package com.aliernfrog.pftool.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun RadioButtons(options: List<String>, columnColor: Color = MaterialTheme.colors.secondary, selectedIndex: Int = 0, onSelect: (String) -> Unit) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(options[selectedIndex]) }
    ColumnRounded(color = columnColor) {
        options.forEach { option ->
            val isSelected = selectedOption === option
            val onSelected = {
                onOptionSelected(option)
                onSelect(option)
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = isSelected, onClick = { onSelected() })) {
                RadioButton(colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.primary),
                    selected = isSelected,
                    onClick = { onSelected() }
                )
                Text(text = option, fontWeight = FontWeight.Bold)
            }
        }
    }
}