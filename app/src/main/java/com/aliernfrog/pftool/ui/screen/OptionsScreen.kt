package com.aliernfrog.pftool.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.aliernfrog.pftool.ui.theme.supportsDynamicTheme
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
    val themeOptions = listOf(context.getString(R.string.optionsThemeSystem),context.getString(R.string.optionsThemeLight),context.getString(R.string.optionsThemeDark))
    val themeChosen = config.getInt(ConfigKey.KEY_APP_THEME, Theme.SYSTEM)
    val dynamicTheme = remember { mutableStateOf(config.getBoolean(ConfigKey.KEY_APP_DYNAMIC_COLORS, true)) }
    PFToolColumnRounded(title = context.getString(R.string.optionsTheme)) {
        PFToolRadioButtons(options = themeOptions, initialIndex = themeChosen, onSelect = { option ->
            config.edit().putInt(ConfigKey.KEY_APP_THEME, option).apply()
            onThemeUpdate(context)
        })
        if (supportsDynamicTheme) {
            PFToolSwitch(
                title = context.getString(R.string.optionsThemeDynamicTheme),
                checked = dynamicTheme.value
            ) {
                dynamicTheme.value = it
                config.edit().putBoolean(ConfigKey.KEY_APP_DYNAMIC_COLORS, it).apply()
                onThemeUpdate(context)
            }
        }
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
            PFToolButton(title = it.name, painter = icon, containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary) {
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
    val veryHiddenSwitchChecked = remember { mutableStateOf(false) }
    PFToolColumnRounded(title = context.getString(R.string.optionsExperimental)) {
        prefEdits.forEach { key ->
            val value = remember { mutableStateOf(config.getString(key, "")!!) }
            PFToolTextField(label = { Text(text = "Prefs: $key") }, value = value.value, onValueChange = {
                value.value = it
                configEditor.putString(key, it)
                configEditor.apply()
            })
        }
        PFToolButtonCentered(title = context.getString(R.string.optionsExperimentalResetPrefs), containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError) {
            prefEdits.forEach { key ->
                configEditor.remove(key)
                configEditor.apply()
            }
            restartApp(context)
        }
        PFToolSwitch(
            title = context.getString(R.string.optionsExperimentalSwitch),
            description = context.getString(R.string.optionsExperimentalSwitchDescription),
            checked = veryHiddenSwitchChecked.value,
            onCheckedChange = { veryHiddenSwitchChecked.value = it }
        )
    }
}

private fun onThemeUpdate(context: Context) {
    topToastManager.showToast(context.getString(R.string.optionsThemeChanged), iconDrawableId = R.drawable.check, iconTintColorType = TopToastColorType.PRIMARY) { restartApp(context) }
}

private fun restartApp(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    (context as Activity).finish()
    context.startActivity(intent)
}