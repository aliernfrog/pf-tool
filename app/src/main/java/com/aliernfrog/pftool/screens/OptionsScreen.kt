package com.aliernfrog.pftool.screens

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.composables.BaseScaffold
import com.aliernfrog.pftool.composables.ColumnRounded
import com.aliernfrog.pftool.composables.RadioButtons

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
    ColumnRounded(color = MaterialTheme.colors.secondary, title = context.getString(R.string.optionsTheme)) {
        RadioButtons(options = options, columnColor = MaterialTheme.colors.secondaryVariant, onSelect = {
            //TODO
        })
    }
}