package com.aliernfrog.pftool.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aliernfrog.pftool.*
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.*
import com.aliernfrog.pftool.ui.theme.supportsDynamicTheme
import com.aliernfrog.pftool.util.GeneralUtil
import com.aliernfrog.toptoast.TopToastColorType
import com.aliernfrog.toptoast.TopToastManager

private lateinit var topToastManager: TopToastManager

private val aboutClickCount = mutableStateOf(0)
private val forceShowDynamicColorsOption = mutableStateOf(false)

private const val experimentalRequiredClicks = 10

@Composable
fun OptionsScreen(navController: NavController, toastManager: TopToastManager, config: SharedPreferences) {
    topToastManager = toastManager
    PFToolBaseScaffold(title = LocalContext.current.getString(R.string.options), navController = navController) {
        ThemeOptions(config)
        AboutPFTool()
        if (aboutClickCount.value >= experimentalRequiredClicks) ExperimentalOptions(config)
    }
}

@Composable
private fun ThemeOptions(config: SharedPreferences) {
    val context = LocalContext.current
    val themeOptions = listOf(context.getString(R.string.optionsThemeSystem),context.getString(R.string.optionsThemeLight),context.getString(R.string.optionsThemeDark))
    val themeChosen = config.getInt(ConfigKey.KEY_APP_THEME, Theme.SYSTEM)
    val dynamicTheme = remember { mutableStateOf(config.getBoolean(ConfigKey.KEY_APP_DYNAMIC_COLORS, true)) }
    OptionsColumn(title = context.getString(R.string.optionsTheme), bottomDivider = true) {
        PFToolRadioButtons(options = themeOptions, initialIndex = themeChosen, onSelect = { option ->
            config.edit().putInt(ConfigKey.KEY_APP_THEME, option).apply()
            onThemeUpdate(context)
        })
        if (forceShowDynamicColorsOption.value || supportsDynamicTheme) {
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
    OptionsColumn(title = context.getString(R.string.optionsAbout), bottomDivider = aboutClickCount.value >= experimentalRequiredClicks) {
        Text(text = fullText, Modifier.clickable {
            aboutClickCount.value++
            if (aboutClickCount.value == experimentalRequiredClicks) topToastManager.showToast(context.getString(R.string.optionsExperimentalEnabled))
        }.padding(horizontal = 16.dp))
        Links()
    }
}

@Composable
private fun Links() {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val linksVisible = remember { mutableStateOf(false) }
    OptionsButton(title = context.getString(R.string.optionsLinks), painter = painterResource(id = R.drawable.share)) {
        linksVisible.value = !linksVisible.value
    }
    AnimatedVisibility(visible = linksVisible.value) {
        PFToolColumnRounded(padding = 0.dp) {
            Link.socials.forEach {
                val icon = when(it.url.split("/")[2]) {
                    "discord.gg" -> painterResource(id = R.drawable.discord)
                    "github.com" -> painterResource(id = R.drawable.github)
                    else -> null
                }
                OptionsButton(title = it.name, painter = icon, contentColor = MaterialTheme.colorScheme.onSurfaceVariant) { uriHandler.openUri(it.url) }
            }
        }
    }
}

@Composable
private fun ExperimentalOptions(config: SharedPreferences) {
    val context = LocalContext.current
    val configEditor = config.edit()
    val prefEdits = listOf(ConfigKey.KEY_MAPS_DIR,ConfigKey.KEY_MAPS_EXPORT_DIR)
    OptionsColumn(title = context.getString(R.string.optionsExperimental), bottomDivider = false) {
        PFToolSwitch(
            title = context.getString(R.string.optionsExperimentalShowDynamicColorsOption),
            checked = forceShowDynamicColorsOption.value,
            onCheckedChange = {
                forceShowDynamicColorsOption.value = it
            }
        )
        PFToolColumnRounded {
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
        }
    }
}

@Composable
private fun OptionsColumn(title: String, bottomDivider: Boolean, content: @Composable ColumnScope.() -> Unit) {
    Text(text = title, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    Column(Modifier.animateContentSize(), content = content)
    if (bottomDivider) Divider(modifier = Modifier.padding(16.dp).alpha(0.7f), thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
}

@Composable
private fun OptionsButton(title: String, painter: Painter? = null, contentColor: Color = MaterialTheme.colorScheme.onSurface, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        if (painter != null) Image(painter, title, Modifier.padding(end = 4.dp).size(40.dp).padding(4.dp), colorFilter = ColorFilter.tint(contentColor))
        Text(title, color = contentColor, fontSize = 16.sp)
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