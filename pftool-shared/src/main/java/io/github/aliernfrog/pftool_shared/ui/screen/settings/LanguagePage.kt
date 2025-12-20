package io.github.aliernfrog.pftool_shared.ui.screen.settings

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.WavyProgressIndicatorDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.pftool_shared.data.Language
import io.github.aliernfrog.pftool_shared.data.getAvailableLanguage
import io.github.aliernfrog.pftool_shared.data.getNameIn
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.pftool_shared.util.extension.copy
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppSmallTopBar
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.ui.component.verticalSegmentedShape
import io.github.aliernfrog.shared.ui.theme.AppComponentShape
import io.github.aliernfrog.shared.util.getSharedString
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import io.github.aliernfrog.shared.util.sharedStringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePage(
    crowdinURL: String,
    currentLanguagePref: BasePreferenceManager.Preference<String>,
    languages: List<Language>,
    appLanguage: Language?,
    deviceLanguage: Language?,
    baseLanguage: Language,
    onSetLanguageRequest: (Language?) -> Unit,
    onNavigateBackRequest: () -> Unit
) {
    val availableDeviceLanguage = remember {
        deviceLanguage?.getAvailableLanguage(languages = languages)
    }

    @Composable
    fun LanguageButton(
        modifier: Modifier = Modifier,
        language: Language? = null,
        title: String = language?.localizedName.toString(),
        description: String = language?.fullCode.toString(),
        painter: Painter = rememberVectorPainter(Icons.Default.Translate),
        selected: Boolean = language?.fullCode == currentLanguagePref.value,
        onClick: () -> Unit = {
            onSetLanguageRequest(language)
        }
    ) {
        ExpressiveButtonRow(
            title = title,
            description = description,
            icon = {
                val density = LocalDensity.current
                var iconWidth by remember { mutableStateOf(0.dp) }
                Box {
                    ExpressiveRowIcon(
                        painter = painter,
                        modifier = Modifier.onSizeChanged {
                            iconWidth = with(density) {
                                it.width.toDp()
                            }
                        }
                    )
                    language?.let {
                        TranslationProgressIndicator(
                            progress = it.translationProgress,
                            isBase = baseLanguage.languageCode == language.languageCode,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(iconWidth)
                        )
                    }
                }
            },
            trailingComponent = {
                RadioButton(
                    selected = selected,
                    onClick = onClick
                )
            },
            onClick = onClick,
            modifier = modifier
        )
    }

    AppScaffold(
        topBar = { scrollBehavior ->
            AppSmallTopBar(
                title = sharedStringResource(PFToolSharedString.SettingsLanguage),
                scrollBehavior = scrollBehavior,
                onNavigationClick = onNavigateBackRequest
            )
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ) {
        LazyColumn {
            item {
                TranslationHelp(
                    isDeviceLanguageAvailable = availableDeviceLanguage != null,
                    crowdinURL = crowdinURL
                )
            }

            item {
                ExpressiveSection(sharedStringResource(PFToolSharedString.SettingsLanguageSystem)) {
                    VerticalSegmentor(
                        {
                            LanguageButton(
                                language = deviceLanguage,
                                title = sharedStringResource(PFToolSharedString.SettingsLanguageSystemFollow),
                                description = availableDeviceLanguage?.localizedName ?: sharedStringResource(PFToolSharedString.SettingsLanguageSystemNotAvailable)
                                    .replace("{SYSTEM_LANGUAGE}", appLanguage?.let {
                                        deviceLanguage?.getNameIn(it.languageCode, it.countryCode)
                                    } ?: ""),
                                painter = rememberVectorPainter(Icons.Default.PhoneAndroid),
                                selected = currentLanguagePref.value.isBlank(),
                                onClick = {
                                    onSetLanguageRequest(null)
                                }
                            )
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                ExpressiveSection(sharedStringResource(PFToolSharedString.SettingsLanguageOther)) {}
            }

            itemsIndexed(languages) { index, language ->
                LanguageButton(
                    language = language,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .verticalSegmentedShape(index = index, totalSize = languages.size)
                )
            }

            item {
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TranslationProgressIndicator(
    progress: Float,
    isBase: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val state = rememberTooltipState(isPersistent = true)
    val tooltipText = remember {
        if (isBase) context.getSharedString(PFToolSharedString.SettingsLanguageProgressBase)
        else context.getSharedString(PFToolSharedString.SettingsLanguageProgressPercent)
            .replace("{PERCENT}", (progress*100).toInt().toString())
    }

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(tooltipText)
            }
        },
        state = state
    ) {
        CircularWavyProgressIndicator(
            progress = { progress },
            stroke = WavyProgressIndicatorDefaults.circularIndicatorStroke.copy(
                width = with(density) {
                    2.dp.toPx()
                }
            ),
            amplitude = {
                if (state.isVisible) 0.8f else 0f
            },
            waveSpeed = 5.dp,
            modifier = modifier
                .combinedClickable(
                    onLongClick = {
                        scope.launch { state.show() }
                    },
                    onClick = {
                        scope.launch { state.show() }
                    }
                )
        )
    }
}

@Composable
fun TranslationHelp(
    isDeviceLanguageAvailable: Boolean,
    crowdinURL: String
) {
    val uriHandler = LocalUriHandler.current
    Card(
        shape = AppComponentShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
        onClick = { uriHandler.openUri(crowdinURL) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.Handshake),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = sharedStringResource(
                        if (isDeviceLanguageAvailable) PFToolSharedString.SettingsLanguageHelp
                        else PFToolSharedString.SettingsLanguageHelpDeviceNotAvailable
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = sharedStringResource(PFToolSharedString.SettingsLanguageHelpDescription),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}