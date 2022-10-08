package com.aliernfrog.pftool.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.PFToolComposableShape

@Composable
fun PFToolRadioButtons(
    options: List<String>,
    initialIndex: Int = 0,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    contentColor: Color = MaterialTheme.colorScheme.onSecondary,
    onSelect: (Int) -> Unit
) {
    val (selectedIndex, onOptionSelect) = remember { mutableStateOf(initialIndex) }
    Column(Modifier.fillMaxWidth().padding(8.dp).clip(PFToolComposableShape).background(backgroundColor).padding(8.dp)) {
        options.forEachIndexed { index, option ->
            val selected = selectedIndex == index
            val onSelected = { onOptionSelect(index); onSelect(index) }
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(PFToolComposableShape)
                    .clickable { onSelected() }) {
                RadioButton(colors = RadioButtonDefaults.colors(selectedColor = contentColor, unselectedColor = contentColor.copy(0.5f), disabledSelectedColor = contentColor.copy(0.7f), disabledUnselectedColor = contentColor.copy(0.5f)),
                    selected = selected,
                    onClick = { onSelected() }
                )
                Text(text = option, fontWeight = FontWeight.Bold, color = contentColor)
            }
        }
    }
}