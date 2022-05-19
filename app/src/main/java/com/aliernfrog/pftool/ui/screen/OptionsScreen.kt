package com.aliernfrog.pftool.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolRadioButtons

@Composable
fun OptionsScreen(navController: NavController) {
    PFToolBaseScaffold(title = LocalContext.current.getString(R.string.options), navController = navController) {
        ThemeSelection()
    }
}

@Composable
private fun ThemeSelection() {
    val context = LocalContext.current
    val options = listOf(context.getString(R.string.optionsThemeSystem),context.getString(R.string.optionsThemeLight),context.getString(R.string.optionsThemeDark))
    PFToolColumnRounded(color = MaterialTheme.colors.secondary, title = context.getString(R.string.optionsTheme)) {
        PFToolRadioButtons(options = options, selectedIndex = getThemePreference(context), columnColor = MaterialTheme.colors.secondaryVariant, onSelect = { option ->
            applyTheme(option, context)
        })
    }
}

private fun getThemePreference(context: Context): Int {
    val prefs = context.getSharedPreferences("APP_CONFIG", Context.MODE_PRIVATE)
    return prefs.getInt("appTheme", 0)
}

private fun applyTheme(option: String, context: Context) {
    val prefsEditor = context.getSharedPreferences("APP_CONFIG", Context.MODE_PRIVATE).edit()
    var theme = 0 //system
    if (option == context.getString(R.string.optionsThemeLight)) theme = 1 //light
    if (option == context.getString(R.string.optionsThemeDark)) theme = 2 //dark
    prefsEditor.putInt("appTheme", theme)
    prefsEditor.apply()
    Toast.makeText(context, context.getString(R.string.optionsThemeChanged), Toast.LENGTH_SHORT).show()
}