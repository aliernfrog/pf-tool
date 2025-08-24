package com.aliernfrog.pftool.ui.screen.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.component.VerticalSegmentor
import com.aliernfrog.pftool.ui.component.expressive.ExpressiveRowIcon
import com.aliernfrog.pftool.ui.component.expressive.ExpressiveSection
import com.aliernfrog.pftool.ui.component.expressive.ExpressiveSwitchRow
import com.aliernfrog.pftool.ui.component.expressive.toRowFriendlyColor
import com.aliernfrog.pftool.ui.theme.AppRoundnessSize
import com.aliernfrog.pftool.ui.theme.Theme
import com.aliernfrog.pftool.ui.theme.supportsMaterialYou
import com.aliernfrog.pftool.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppearancePage(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    SettingsPageContainer(
        title = stringResource(R.string.settings_appearance),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        ExpressiveSection(
            title = stringResource(R.string.settings_appearance_theme)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Theme.entries.forEach { theme ->
                    val selected = settingsViewModel.prefs.theme.value == theme.ordinal
                    val containerColor = animateColorAsState(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                    val contentColor = animateColorAsState(
                        if (selected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
                    )
                    val shape = animateDpAsState(
                        if (selected) AppRoundnessSize+16.dp else AppRoundnessSize
                    )

                    CompositionLocalProvider(LocalContentColor provides contentColor.value) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(shape.value))
                                .background(containerColor.value)
                                .clickable {
                                    settingsViewModel.prefs.theme.value = theme.ordinal
                                }
                                .padding(vertical = 32.dp)
                        ) {
                            AnimatedContent(
                                targetState = selected
                            ) { useFilled ->
                                Icon(
                                    imageVector = if (useFilled) theme.filledIcon else theme.outlinedIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Text(
                                text = stringResource(theme.label),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        ExpressiveSection(
            title = stringResource(R.string.settings_appearance_colors)
        ) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = stringResource(R.string.settings_appearance_materialYou),
                        description = stringResource(
                            if (supportsMaterialYou) R.string.settings_appearance_materialYou_description
                            else R.string.settings_appearance_materialYou_unavailable
                        ),
                        icon = {
                            ExpressiveRowIcon(
                                painter = rememberVectorPainter(Icons.Rounded.Brush),
                                containerColor = Color.Yellow.toRowFriendlyColor
                            )
                        },
                        checked = settingsViewModel.prefs.materialYou.value,
                        enabled = supportsMaterialYou
                    ) {
                        settingsViewModel.prefs.materialYou.value = it
                    }
                },
                {
                    ExpressiveSwitchRow(
                        title = stringResource(R.string.settings_appearance_pitchBlack),
                        description = stringResource(R.string.settings_appearance_pitchBlack_description),
                        icon = {
                            ExpressiveRowIcon(
                                painter = rememberVectorPainter(Icons.Rounded.Contrast),
                                containerColor = Color.Black.toRowFriendlyColor
                            )
                        },
                        checked = settingsViewModel.prefs.pitchBlack.value
                    ) {
                        settingsViewModel.prefs.pitchBlack.value = it
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}