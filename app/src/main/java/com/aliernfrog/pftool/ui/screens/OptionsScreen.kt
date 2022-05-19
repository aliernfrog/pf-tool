package com.aliernfrog.pftool.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composables.BaseScaffold
import com.aliernfrog.pftool.ui.composables.ColumnRounded
import com.aliernfrog.pftool.ui.composables.RadioButtons

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
        RadioButtons(options = options, selectedIndex = getThemePreference(context), columnColor = MaterialTheme.colors.secondaryVariant, onSelect = { option ->
            applyTheme(option, context)
        })
    }
}

fun getThemePreference(context: Context): Int {
    val prefs = context.getSharedPreferences("APP_CONFIG", Context.MODE_PRIVATE)
    return prefs.getInt("appTheme", 0)
}

fun applyTheme(option: String, context: Context) {
    val prefsEditor = context.getSharedPreferences("APP_CONFIG", Context.MODE_PRIVATE).edit()
    var theme = 0 //system
    if (option == context.getString(R.string.optionsThemeLight)) theme = 1 //light
    if (option == context.getString(R.string.optionsThemeDark)) theme = 2 //dark
    prefsEditor.putInt("appTheme", theme)
    prefsEditor.apply()
    Toast.makeText(context, context.getString(R.string.optionsThemeChanged), Toast.LENGTH_SHORT).show()
}