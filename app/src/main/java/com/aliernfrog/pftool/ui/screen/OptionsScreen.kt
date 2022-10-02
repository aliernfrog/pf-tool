package com.aliernfrog.pftool.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aliernfrog.pftool.*
import com.aliernfrog.pftool.ui.composable.*
import com.aliernfrog.pftool.util.GeneralUtil
import com.aliernfrog.toptoast.TopToastColorType
import com.aliernfrog.toptoast.TopToastManager

private lateinit var topToastManager: TopToastManager

private val aboutClickCount = mutableStateOf(0)

private const val experimentalRequiredClicks = 10

@Composable
fun OptionsScreen(navController: NavController, toastManager: TopToastManager, config: SharedPreferences) {
    topToastManager = toastManager
    PFToolBaseScaffold(title = LocalContext.current.getString(R.string.options), navController = navController) {
        ThemeSelection(config)
        AboutPFTool()
        Links()
        if (aboutClickCount.value >= experimentalRequiredClicks) ExperimentalOptions(config)
    }
}

@Composable
private fun ThemeSelection(config: SharedPreferences) {
    val context = LocalContext.current
    val options = listOf(context.getString(R.string.optionsThemeSystem),context.getString(R.string.optionsThemeLight),context.getString(R.string.optionsThemeDark))
    val chosen = config.getInt(ConfigKey.KEY_APP_THEME, Theme.SYSTEM)
    PFToolColumnRounded(color = MaterialTheme.colors.secondary, title = context.getString(R.string.optionsTheme)) {
        PFToolRadioButtons(options = options, selectedIndex = chosen, columnColor = MaterialTheme.colors.secondaryVariant, onSelect = { option ->
            applyTheme(option, config, context)
        })
    }
}

@Composable
private fun AboutPFTool() {
    val context = LocalContext.current
    val version = "${GeneralUtil.getAppVersionName(context)} (${GeneralUtil.getAppVersionCode(context)})"
    val fullText = "${context.getString(R.string.optionsAboutInfo)}\n${context.getString(R.string.optionsAboutVersion)}: $version"
    PFToolColumnRounded(title = context.getString(R.string.optionsAbout), onClick = {
        aboutClickCount.value++
        if (aboutClickCount.value == experimentalRequiredClicks) topToastManager.showToast(context.getString(R.string.optionsExperimentalEnabled))
    }) {
        Text(text = fullText, Modifier.padding(horizontal = 8.dp))
    }
}

@Composable
private fun Links() {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    PFToolColumnRounded(title = context.getString(R.string.optionsLinks)) {
        Link.socials.forEach {
            val icon = when(it.url.split("/")[2]) {
                "discord.gg" -> painterResource(id = R.drawable.discord)
                "github.com" -> painterResource(id = R.drawable.github)
                else -> null
            }
            PFToolButton(title = it.name, painter = icon, backgroundColor = MaterialTheme.colors.secondaryVariant) {
                uriHandler.openUri(it.url)
            }
        }
    }
}

@Composable
private fun ExperimentalOptions(config: SharedPreferences) {
    val context = LocalContext.current
    val configEditor = config.edit()
    val prefEdits = listOf(ConfigKey.KEY_MAPS_DIR,ConfigKey.KEY_MAPS_EXPORT_DIR)
    PFToolColumnRounded(title = context.getString(R.string.optionsExperimental)) {
        prefEdits.forEach { key ->
            val value = remember { mutableStateOf(config.getString(key, "")!!) }
            PFToolTextField(label = { Text(text = "Prefs: $key") }, value = value.value, onValueChange = {
                value.value = it
                configEditor.putString(key, it)
                configEditor.apply()
            })
        }
        PFToolButtonCentered(title = context.getString(R.string.optionsExperimentalResetPrefs), backgroundColor = MaterialTheme.colors.error, contentColor = MaterialTheme.colors.onError) {
            prefEdits.forEach { key ->
                configEditor.remove(key)
                configEditor.apply()
            }
            restartApp(context)
        }
    }
}

private fun applyTheme(option: String, config: SharedPreferences, context: Context) {
    val configEditor = config.edit()
    val theme = when(option) {
        context.getString(R.string.optionsThemeLight) -> Theme.LIGHT
        context.getString(R.string.optionsThemeDark) -> Theme.DARK
        else -> Theme.SYSTEM
    }
    configEditor.putInt(ConfigKey.KEY_APP_THEME, theme)
    configEditor.apply()
    topToastManager.showToast(context.getString(R.string.optionsThemeChanged), iconDrawableId = R.drawable.check, iconBackgroundColorType = TopToastColorType.PRIMARY) { restartApp(context) }
}

private fun restartApp(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    (context as Activity).finish()
    context.startActivity(intent)
}