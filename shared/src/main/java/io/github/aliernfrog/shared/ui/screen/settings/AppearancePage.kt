package io.github.aliernfrog.shared.ui.screen.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.Gradient
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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
import io.github.aliernfrog.shared.util.nullableSharedStringResource
import kotlinx.coroutines.launch

@Composable
fun AppearancePage(
    themePref: BasePreferenceManager.Preference<Int>,
    materialYouPref: BasePreferenceManager.Preference<Boolean>,
    pitchBlackPref: BasePreferenceManager.Preference<Boolean>,
    blurPref: BasePreferenceManager.Preference<Boolean>,
    bottomGradientPref: BasePreferenceManager.Preference<Boolean>?,
    onNavigateBackRequest: () -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    SettingsPageContainer(
        title = sharedStringResource(SharedString::settingsAppearance),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        ExpressiveSection(
            title = sharedStringResource(SharedString::settingsAppearanceTheme)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
            ) {
                Theme.entries.forEachIndexed { index, theme ->
                    val isLightThemeItem = theme == Theme.LIGHT
                    val selected = themePref.value == theme.ordinal

                    val interactionSource = remember { MutableInteractionSource() }
                    val pressed by interactionSource.collectIsPressedAsState()
                    val weight by animateFloatAsState(if (selected) 1.1f else 1f)
                    val containerColor by animateColorAsState(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceContainer
                    )
                    val contentColor by animateColorAsState(
                        if (selected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
                    )

                    var height by remember { mutableStateOf(200.dp) }
                    val roundness by animateDpAsState(
                        if (pressed) 4.dp
                        else if (selected) height / 2
                        else 8.dp
                    )
                    val iconRotation = remember { Animatable(0f) }

                    val onSelect = {
                        themePref.value = theme.ordinal
                        if (isLightThemeItem) scope.launch {
                            iconRotation.animateTo(
                                targetValue = iconRotation.targetValue+90f,
                                animationSpec = tween(durationMillis = 800, easing = EaseInOut)
                            )
                        }
                    }

                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                            modifier = Modifier
                                .onSizeChanged {
                                    with(density) {
                                        it.height.toDp()
                                    }
                                }
                                .weight(weight)
                                .semantics { Role.RadioButton }
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(
                                    topStart = if (index == 0) 200.dp else roundness,
                                    bottomStart = if (index == 0) 200.dp else roundness,
                                    topEnd = if (index == Theme.entries.lastIndex) 200.dp else roundness,
                                    bottomEnd = if (index == Theme.entries.lastIndex) 200.dp else roundness
                                ))
                                .background(containerColor)
                                .clickable(
                                    interactionSource = interactionSource,
                                    onClick = { onSelect() }
                                )
                                .padding(top = 36.dp)
                        ) {
                            @Composable
                            fun ThemeIcon(useFilled: Boolean) {
                                Icon(
                                    imageVector = if (useFilled) theme.filledIcon else theme.outlinedIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp).graphicsLayer {
                                        rotationZ = iconRotation.value
                                    }
                                )
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

        ExpressiveSection(
            title = sharedStringResource(SharedString::settingsAppearanceColors)
        ) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(SharedString::settingsAppearanceColorsMaterialYou),
                        description = sharedStringResource(
                            if (supportsMaterialYou) SharedString::settingsAppearanceColorsMaterialYouDescription
                            else SharedString::settingsAppearanceColorsMaterialYouUnavailable
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
                        title = sharedStringResource(SharedString::settingsAppearanceColorsPitchBlack),
                        description = sharedStringResource(SharedString::settingsAppearanceColorsPitchBlackDescription),
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

        ExpressiveSection(
            title = sharedStringResource(SharedString::settingsAppearanceEffects)
        ) {
            val effectsOptions: Array<(@Composable () -> Unit)?> = remember { arrayOf(
                {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(SharedString::settingsAppearanceEffectsBlur),
                        description = sharedStringResource(SharedString::settingsAppearanceEffectsBlurDescription),
                        icon = {
                            ExpressiveRowIcon(
                                painter = rememberVectorPainter(Icons.Rounded.BlurOn),
                                containerColor = Color.Magenta.toRowFriendlyColor
                            )
                        },
                        checked = blurPref.value,
                        onCheckedChange = { blurPref.value = it }
                    )
                },
                bottomGradientPref?.let { pref ->
                    {
                        ExpressiveSwitchRow(
                            title = nullableSharedStringResource(
                                SharedString::settingsAppearanceEffectsBottomGradient,
                                "Bottom gradient"
                            ),
                            description = nullableSharedStringResource(
                                SharedString::settingsAppearanceEffectsBottomGradientDescription,
                                "Bottom gradient under the floating toolbar"
                            ),
                            icon = {
                                ExpressiveRowIcon(
                                    painter = rememberVectorPainter(Icons.Rounded.Gradient),
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            },
                            checked = pref.value,
                            onCheckedChange = { pref.value = it }
                        )
                    }
                }
            ) }

            VerticalSegmentor(
                *effectsOptions.filterNotNull().toTypedArray(),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}