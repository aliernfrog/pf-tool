package com.aliernfrog.pftool.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aliernfrog.pftool.MainActivity
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolRadioButtons
import com.aliernfrog.pftool.utils.AppUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private lateinit var scope: CoroutineScope
private lateinit var scaffoldState: ScaffoldState

private val aboutClickCount = mutableStateOf(0)

private const val experimentalRequiredClicks = 10

@Composable
fun OptionsScreen(navController: NavController, config: SharedPreferences) {
    scope = rememberCoroutineScope()
    scaffoldState = rememberScaffoldState()
    PFToolBaseScaffold(title = LocalContext.current.getString(R.string.options), state = scaffoldState, navController = navController) {
        ThemeSelection(config)
        AboutPFTool()
        if (aboutClickCount.value >= experimentalRequiredClicks) ExperimentalOptions()
    }
}

@Composable
private fun ThemeSelection(config: SharedPreferences) {
    val context = LocalContext.current
    val options = listOf(context.getString(R.string.optionsThemeSystem),context.getString(R.string.optionsThemeLight),context.getString(R.string.optionsThemeDark))
    val chosen = config.getInt("appTheme", 0)
    PFToolColumnRounded(color = MaterialTheme.colors.secondary, title = context.getString(R.string.optionsTheme)) {
        PFToolRadioButtons(options = options, selectedIndex = chosen, columnColor = MaterialTheme.colors.secondaryVariant, onSelect = { option ->
            applyTheme(option, config, context)
        })
    }
}

@Composable
private fun AboutPFTool() {
    val context = LocalContext.current
    val version = "${AppUtil.getAppVersionName(context)} (${AppUtil.getAppVersionCode(context)})"
    val fullText = "${context.getString(R.string.optionsAboutInfo)}\n${context.getString(R.string.optionsAboutVersion)}: $version"
    PFToolColumnRounded(title = context.getString(R.string.optionsAbout), onClick = {
        aboutClickCount.value++
        if (aboutClickCount.value == experimentalRequiredClicks) scope.launch { scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.optionsExperimentalEnabled)) }
    }) {
        Text(text = fullText, Modifier.padding(horizontal = 8.dp))
    }
}

@Composable
private fun ExperimentalOptions() {
    val context = LocalContext.current
    PFToolColumnRounded(title = context.getString(R.string.optionsExperimental)) {
        //TODO
    }
}

private fun applyTheme(option: String, config: SharedPreferences, context: Context) {
    val configEditor = config.edit()
    var theme = 0 //system
    if (option == context.getString(R.string.optionsThemeLight)) theme = 1 //light
    if (option == context.getString(R.string.optionsThemeDark)) theme = 2 //dark
    configEditor.putInt("appTheme", theme)
    configEditor.apply()
    scope.launch {
        when(scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.optionsThemeChanged), context.getString(R.string.action_restartNow))) {
            SnackbarResult.ActionPerformed -> { restartApp(context) }
            SnackbarResult.Dismissed -> {  }
        }
    }
}

private fun restartApp(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    (context as Activity).finish()
    context.startActivity(intent)
}