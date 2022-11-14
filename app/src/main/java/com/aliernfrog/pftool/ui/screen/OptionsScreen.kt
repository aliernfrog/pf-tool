package com.aliernfrog.pftool.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aliernfrog.pftool.*
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PrefEditItem
import com.aliernfrog.pftool.ui.composable.*
import com.aliernfrog.pftool.ui.theme.supportsMaterialYou
import com.aliernfrog.pftool.util.GeneralUtil
import com.aliernfrog.toptoast.TopToastColorType
import com.aliernfrog.toptoast.TopToastManager

private lateinit var topToastManager: TopToastManager

private val aboutClickCount = mutableStateOf(0)
private val forceShowMaterialYouOption = mutableStateOf(false)

private const val experimentalRequiredClicks = 10

@Composable
fun OptionsScreen(navController: NavController, toastManager: TopToastManager, config: SharedPreferences) {
    topToastManager = toastManager
    PFToolBaseScaffold(title = LocalContext.current.getString(R.string.options), navController = navController) {
        ThemeOptions(config)
        MapsOptions(config)
        AboutPFTool()
        if (aboutClickCount.value >= experimentalRequiredClicks) ExperimentalOptions(config)
    }
}

@Composable
private fun ThemeOptions(config: SharedPreferences) {
    val context = LocalContext.current
    val themeOptions = listOf(context.getString(R.string.optionsThemeSystem),context.getString(R.string.optionsThemeLight),context.getString(R.string.optionsThemeDark))
    val themeChosen = config.getInt(ConfigKey.KEY_APP_THEME, Theme.SYSTEM)
    val dynamicTheme = remember { mutableStateOf(config.getBoolean(ConfigKey.KEY_APP_MATERIAL_YOU, true)) }
    OptionsColumn(title = context.getString(R.string.optionsTheme), modifier = Modifier.animateContentSize()) {
        PFToolRadioButtons(options = themeOptions, initialIndex = themeChosen, onSelect = { option ->
            config.edit().putInt(ConfigKey.KEY_APP_THEME, option).apply()
            onThemeUpdate(context)
        })
        if (forceShowMaterialYouOption.value || supportsMaterialYou) {
            PFToolSwitch(
                title = context.getString(R.string.optionsThemeMaterialYou),
                description = context.getString(R.string.optionsThemeMaterialYouDescription),
                checked = dynamicTheme.value
            ) {
                dynamicTheme.value = it
                config.edit().putBoolean(ConfigKey.KEY_APP_MATERIAL_YOU, it).apply()
                onThemeUpdate(context)
            }
        }
    }
}

@Composable
private fun MapsOptions(config: SharedPreferences) {
    val context = LocalContext.current
    val thumbnailsList = remember { mutableStateOf(config.getBoolean(ConfigKey.KEY_SHOW_MAP_THUMBNAILS_LIST, true)) }
    OptionsColumn(title = context.getString(R.string.optionsMaps)) {
        PFToolSwitch(
            title = context.getString(R.string.optionsMapsShowMapThumbnailsList),
            description = context.getString(R.string.optionsMapsShowMapThumbnailsListDescription),
            checked = thumbnailsList.value
        ) {
            thumbnailsList.value = it
            config.edit().putBoolean(ConfigKey.KEY_SHOW_MAP_THUMBNAILS_LIST, it).apply()
        }
    }
}

@Composable
private fun AboutPFTool() {
    val context = LocalContext.current
    val version = "v${GeneralUtil.getAppVersionName(context)} (${GeneralUtil.getAppVersionCode(context)})"
    OptionsColumn(title = context.getString(R.string.optionsAbout), bottomDivider = false) {
        OptionsButton(title = context.getString(R.string.optionsAboutVersion), description = version) {
            aboutClickCount.value++
            if (aboutClickCount.value == experimentalRequiredClicks) topToastManager.showToast(context.getString(R.string.optionsExperimentalEnabled))
        }
        Links()
    }
}

@Composable
private fun Links() {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val linksVisible = remember { mutableStateOf(false) }
    OptionsButton(title = context.getString(R.string.optionsAboutLinks), description = context.getString(R.string.optionsAboutLinksDescription), expanded = linksVisible.value) {
        linksVisible.value = !linksVisible.value
    }
    AnimatedVisibility(
        visible = linksVisible.value,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        PFToolColumnRounded(Modifier.padding(horizontal = 8.dp)) {
            Link.socials.forEach {
                val icon = when(it.url.split("/")[2]) {
                    "discord.gg" -> painterResource(id = R.drawable.discord)
                    "github.com" -> painterResource(id = R.drawable.github)
                    else -> null
                }
                OptionsButton(title = it.name, painter = icon, rounded = true, contentColor = MaterialTheme.colorScheme.onSurfaceVariant) { uriHandler.openUri(it.url) }
            }
        }
    }
}

@Composable
private fun ExperimentalOptions(config: SharedPreferences) {
    val context = LocalContext.current
    val configEditor = config.edit()
    val prefEdits = listOf(
        PrefEditItem(ConfigKey.KEY_MAPS_DIR, ConfigKey.DEFAULT_MAPS_DIR),
        PrefEditItem(ConfigKey.KEY_MAPS_EXPORT_DIR, ConfigKey.DEFAULT_MAPS_EXPORT_DIR)
    )
    OptionsColumn(title = context.getString(R.string.optionsExperimental), bottomDivider = false, topDivider = true) {
        Text(context.getString(R.string.optionsExperimentalDescription), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp))
        PFToolSwitch(
            title = context.getString(R.string.optionsExperimentalShowMaterialYouOption),
            checked = forceShowMaterialYouOption.value,
            onCheckedChange = {
                forceShowMaterialYouOption.value = it
            }
        )
        prefEdits.forEach { prefEdit ->
            val value = remember { mutableStateOf(config.getString(prefEdit.key, prefEdit.default)!!) }
            PFToolTextField(label = { Text(text = "Prefs: ${prefEdit.key}") }, value = value.value, modifier = Modifier.padding(horizontal = 8.dp),
                contentColor = MaterialTheme.colorScheme.onSurface,
                containerColor = MaterialTheme.colorScheme.surface,
                rounded = false,
                onValueChange = {
                    value.value = it
                    configEditor.putString(prefEdit.key, it)
                    configEditor.apply()
                }
            )
        }
        OptionsButton(title = context.getString(R.string.optionsExperimentalResetPrefs), contentColor = MaterialTheme.colorScheme.error) {
            prefEdits.forEach {
                configEditor.remove(it.key)
                configEditor.apply()
            }
            restartApp(context)
        }
    }
}

@Composable
private fun OptionsColumn(title: String, modifier: Modifier = Modifier, bottomDivider: Boolean = true, topDivider: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    if (topDivider) Divider(modifier = Modifier.padding(16.dp).alpha(0.7f), thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
    Text(text = title, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    Column(modifier, content = content)
    if (bottomDivider) Divider(modifier = Modifier.padding(16.dp).alpha(0.7f), thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun OptionsButton(title: String, description: String? = null, painter: Painter? = null, rounded: Boolean = false, expanded: Boolean? = null, contentColor: Color = MaterialTheme.colorScheme.onSurface, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(44.dp).clip(if (rounded) PFToolComposableShape else RectangleShape).clickable { onClick() }.padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        if (painter != null) Image(painter, title, Modifier.padding(end = 4.dp).size(40.dp).padding(4.dp), colorFilter = ColorFilter.tint(contentColor))
        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp).weight(1f)) {
            Text(text = title, color = contentColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (description != null) Text(text = description, color = contentColor, fontSize = 14.sp)
        }
        if (expanded != null) AnimatedContent(targetState = expanded) {
            Image(Icons.Default.ArrowDropDown, null, modifier = Modifier.rotate(if (it) 180f else 0f), colorFilter = ColorFilter.tint(contentColor))
        }
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