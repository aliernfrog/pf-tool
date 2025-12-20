package io.github.aliernfrog.shared.ui.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSwitchRow
import io.github.aliernfrog.shared.ui.component.expressive.toRowFriendlyColor
import io.github.aliernfrog.shared.ui.theme.Theme
import io.github.aliernfrog.shared.ui.theme.supportsMaterialYou
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import io.github.aliernfrog.shared.util.sharedStringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppearancePage(
    themePref: BasePreferenceManager.Preference<Int>,
    materialYouPref: BasePreferenceManager.Preference<Boolean>,
    pitchBlackPref: BasePreferenceManager.Preference<Boolean>,
    onNavigateBackRequest: () -> Unit
) {
    SettingsPageContainer(
        title = sharedStringResource(SharedString.SettingsAppearance),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        ExpressiveSection(
            title = sharedStringResource(SharedString.SettingsAppearanceTheme)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
            ) {
                Theme.entries.forEachIndexed { index, theme ->
                    val isLightThemeItem = theme == Theme.LIGHT
                    val selected = themePref.value == theme.ordinal
                    val onSelect = { themePref.value = theme.ordinal }
                    val weight by animateFloatAsState(if (selected) 1.1f else 1f)

                    val iconRotation = remember {
                        Animatable(if (isLightThemeItem && selected) 90f else 0f)
                    }

                    LaunchedEffect(selected) {
                        if (isLightThemeItem) iconRotation.animateTo(
                            targetValue = if (selected) 90f else 0f,
                            animationSpec = tween(durationMillis = 800, easing = EaseInOut)
                        )
                    }

                    ToggleButton(
                        checked = selected,
                        onCheckedChange = { onSelect() },
                        shapes = when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            Theme.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                        modifier = Modifier
                            .weight(weight)
                            .animateContentSize()
                            .semantics { Role.RadioButton }
                    ) {
                        Box {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                            ) {
                                @Composable
                                fun ThemeIcon(useFilled: Boolean) {
                                    Icon(imageVector = if (useFilled) theme.filledIcon else theme.outlinedIcon,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp).graphicsLayer {
                                            rotationZ = iconRotation.value
                                        })
                                }

                                if (isLightThemeItem) ThemeIcon(useFilled = selected)
                                else AnimatedContent(targetState = selected) { useFilled ->
                                    ThemeIcon(useFilled = useFilled)
                                }

                                Text(
                                    text = sharedStringResource(theme.label),
                                    style = MaterialTheme.typography.labelLarge
                                )

                                RadioButton(
                                    selected = selected,
                                    onClick = { onSelect() },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = LocalContentColor.current,
                                        unselectedColor = LocalContentColor.current
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        ExpressiveSection(
            title = sharedStringResource(SharedString.SettingsAppearanceColors)
        ) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(SharedString.SettingsAppearanceColorsMaterialYou),
                        description = sharedStringResource(
                            if (supportsMaterialYou) SharedString.SettingsAppearanceColorsMaterialYouDescription
                            else SharedString.SettingsAppearanceColorsMaterialYouUnavailable
                        ),
                        icon = {
                            ExpressiveRowIcon(
                                painter = rememberVectorPainter(Icons.Rounded.Brush),
                                containerColor = Color.Yellow.toRowFriendlyColor
                            )
                        },
                        enabled = supportsMaterialYou,
                        checked = materialYouPref.value,
                        onCheckedChange = { materialYouPref.value = it }
                    )
                }, {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(SharedString.SettingsAppearanceColorsPitchBlack),
                        description = sharedStringResource(SharedString.SettingsAppearanceColorsPitchBlackDescription),
                        icon = {
                            ExpressiveRowIcon(
                                painter = rememberVectorPainter(Icons.Rounded.Contrast),
                                containerColor = Color.Black.toRowFriendlyColor
                            )
                        },
                        checked = pitchBlackPref.value,
                        onCheckedChange = { pitchBlackPref.value = it }
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}