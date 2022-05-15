package com.aliernfrog.pftool.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.composables.BaseScaffold
import com.aliernfrog.pftool.composables.ColumnRounded

@Composable
fun OptionsScreen(navController: NavController) {
    BaseScaffold(title = LocalContext.current.getString(R.string.options), navController = navController) {
        ThemeSelection()
    }
}

@Composable
fun ThemeSelection() {
    val context = LocalContext.current
    val options = listOf(context.getString(R.string.optionsThemeSystem),context.getString(R.string.optionsThemeLight),context.getString(R.string.optionsThemeDark))
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(options[0]) }
    ColumnRounded(color = MaterialTheme.colors.secondary, title = context.getString(R.string.optionsTheme), titleColor = null) {
        options.forEach { option ->
            val isSelected = selectedOption === option
            Row(Modifier.fillMaxWidth()
                .selectable(selected = isSelected, onClick = { onOptionSelected(option) }),
                verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = isSelected,
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.primary),
                    onClick = { onOptionSelected(option) }
                )
                Text(text = option, fontWeight = FontWeight.Bold)
            }
        }
    }
}