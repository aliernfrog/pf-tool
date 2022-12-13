package com.aliernfrog.pftool.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.ripple.rememberRipple
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
import com.aliernfrog.pftool.*
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PrefEditItem
import com.aliernfrog.pftool.state.OptionsState
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolRadioButtons
import com.aliernfrog.pftool.ui.composable.PFToolSwitch
import com.aliernfrog.pftool.ui.composable.PFToolTextField
import com.aliernfrog.pftool.ui.theme.supportsMaterialYou
import com.aliernfrog.pftool.util.staticutil.GeneralUtil
import com.aliernfrog.toptoast.state.TopToastState

private const val experimentalRequiredClicks = 10

@Composable
fun OptionsScreen(config: SharedPreferences, topToastState: TopToastState, optionsState: OptionsState) {
    Column(Modifier.fillMaxSize().verticalScroll(optionsState.scrollState)) {
        ThemeOptions(optionsState)
        MapsOptions(optionsState)
        AboutPFTool(topToastState, optionsState)
        if (optionsState.aboutClickCount.value >= experimentalRequiredClicks) ExperimentalOptions(config, optionsState)
    }
}

@Composable
private fun ThemeOptions(optionsState: OptionsState) {
    val context = LocalContext.current
    val themeOptions = listOf(context.getString(R.string.optionsThemeSystem),context.getString(R.string.optionsThemeLight),context.getString(R.string.optionsThemeDark))
    OptionsColumn(title = context.getString(R.string.optionsTheme), modifier = Modifier.animateContentSize()) {
        PFToolRadioButtons(
            options = themeOptions,
            initialIndex = optionsState.theme.value
        ) {
            optionsState.setTheme(it)
        }
        if (optionsState.forceShowMaterialYouOption.value || supportsMaterialYou) {
            PFToolSwitch(
                title = context.getString(R.string.optionsThemeMaterialYou),
                description = context.getString(R.string.optionsThemeMaterialYouDescription),
                checked = optionsState.materialYou.value
            ) {
                optionsState.setMaterialYou(it)
            }
        }
    }
}

@Composable
private fun MapsOptions(optionsState: OptionsState) {
    val context = LocalContext.current
    OptionsColumn(title = context.getString(R.string.optionsMaps)) {
        PFToolSwitch(
            title = context.getString(R.string.optionsMapsShowMapThumbnailsList),
            description = context.getString(R.string.optionsMapsShowMapThumbnailsListDescription),
            checked = optionsState.showMapThumbnailsInList.value
        ) {
            optionsState.setShowMapThumbnailsInList(it)
        }
    }
}

@Composable
private fun AboutPFTool(topToastState: TopToastState, optionsState: OptionsState) {
    val context = LocalContext.current
    val version = "v${GeneralUtil.getAppVersionName(context)} (${GeneralUtil.getAppVersionCode(context)})"
    OptionsColumn(title = context.getString(R.string.optionsAbout), bottomDivider = false) {
        OptionsButton(title = context.getString(R.string.optionsAboutVersion), description = version) {
            optionsState.aboutClickCount.value++
            if (optionsState.aboutClickCount.value == experimentalRequiredClicks) topToastState.showToast(context.getString(R.string.optionsExperimentalEnabled))
        }
        Links(optionsState)
    }
}

@Composable
private fun Links(optionsState: OptionsState) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    OptionsButton(title = context.getString(R.string.optionsAboutLinks), description = context.getString(R.string.optionsAboutLinksDescription), expanded = optionsState.linksExpanded.value) {
        optionsState.linksExpanded.value = !optionsState.linksExpanded.value
    }
    AnimatedVisibility(
        visible = optionsState.linksExpanded.value,
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
private fun ExperimentalOptions(config: SharedPreferences, optionsState: OptionsState) {
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
            checked = optionsState.forceShowMaterialYouOption.value,
            onCheckedChange = {
                optionsState.forceShowMaterialYouOption.value = it
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

@Composable
private fun OptionsButton(title: String, description: String? = null, painter: Painter? = null, rounded: Boolean = false, expanded: Boolean? = null, contentColor: Color = MaterialTheme.colorScheme.onSurface, onClick: () -> Unit) {
    val arrowRotation = animateFloatAsState(if (expanded == true) 0f else 180f)
    Row(Modifier.fillMaxWidth().heightIn(44.dp).clip(if (rounded) PFToolComposableShape else RectangleShape).clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(color = contentColor),
        onClick = onClick
    ).padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        if (painter != null) Image(painter, title, Modifier.padding(end = 4.dp).size(40.dp).padding(4.dp), colorFilter = ColorFilter.tint(contentColor))
        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp).weight(1f)) {
            Text(text = title, color = contentColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (description != null) Text(text = description, color = contentColor, fontSize = 14.sp)
        }
        if (expanded != null) Image(Icons.Rounded.KeyboardArrowUp, null, modifier = Modifier.rotate(arrowRotation.value), colorFilter = ColorFilter.tint(contentColor))
    }
}

private fun restartApp(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    (context as Activity).finish()
    context.startActivity(intent)
}