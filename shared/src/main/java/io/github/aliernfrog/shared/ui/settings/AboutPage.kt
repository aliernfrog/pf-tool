package io.github.aliernfrog.shared.ui.settings

import android.content.ClipData
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.ContactSupport
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.github.aliernfrog.shared.data.Social
import io.github.aliernfrog.shared.data.getIconPainter
import io.github.aliernfrog.shared.impl.CreditData
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.HorizontalSegmentor
import io.github.aliernfrog.shared.ui.component.ScrollableRow
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowHeader
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSwitchRow
import io.github.aliernfrog.shared.ui.component.expressive.ROW_DEFAULT_ICON_SIZE
import io.github.aliernfrog.shared.ui.component.expressive.toRowFriendlyColor
import io.github.aliernfrog.shared.ui.theme.AppComponentShape
import io.github.aliernfrog.shared.ui.viewmodel.settings.AboutPageViewModel
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.extension.resolveString
import io.github.aliernfrog.shared.util.getSharedString
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import io.github.aliernfrog.shared.util.sharedStringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutPage(
    vm: AboutPageViewModel = koinViewModel(),
    socials: List<Social>,
    credits: List<CreditData>,
    supportLinks: List<Social>,
    debugInfo: String,
    autoCheckUpdatesPref: BasePreferenceManager.Preference<Boolean>,
    experimentalOptionsEnabled: Boolean,
    onExperimentalOptionsEnabled: () -> Unit,
    onShowUpdateSheetRequest: () -> Unit,
    onNavigateLibsRequest: () -> Unit,
    onNavigateBackRequest: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val appIcon = remember {
        context.packageManager.getApplicationIcon(context.packageName).toBitmap().asImageBitmap()
    }

    val availableUpdates = vm.availableUpdates.collectAsStateWithLifecycle().value

    var versionClicks by remember { mutableIntStateOf(0) }

    SettingsPageContainer(
        title = sharedStringResource(SharedString.SettingsAbout),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clip(AppComponentShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    bitmap = appIcon,
                    contentDescription = sharedStringResource(SharedString.AppName),
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(72.dp)
                )
                Column {
                    Text(
                        text = sharedStringResource(SharedString.AppName),
                        style = MaterialTheme.typography.titleLargeEmphasized
                    )
                    Text(
                        text = vm.applicationVersionLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.let {
                            if (experimentalOptionsEnabled) it
                            else it.clickable {
                                versionClicks++
                                if (versionClicks >= 10) onExperimentalOptionsEnabled()
                            }
                        }
                    )
                }
            }

            ChangelogButton(
                updateAvailable = availableUpdates.isNotEmpty(),
                onClick = onShowUpdateSheetRequest
            )

            val socialButtons: List<@Composable () -> Unit> = socials.map { social -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            uriHandler.openUri(social.url)
                        }
                        .padding(vertical = 8.dp)
                ) {
                    ExpressiveRowIcon(
                        painter = social.getIconPainter(),
                        containerColor = social.iconContainerColor.toRowFriendlyColor
                    )
                    Text(social.label.resolveString())
                }
            } }

            HorizontalSegmentor(
                *socialButtons.toTypedArray(),
                itemContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .padding(horizontal = 8.dp)
            )
        }

        ExpressiveSection(
            title = sharedStringResource(SharedString.SettingsAboutUpdates),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = sharedStringResource(SharedString.SettingsAboutUpdatesAutoCheckUpdates),
                        description = sharedStringResource(SharedString.SettingsAboutUpdatesAutoCheckUpdatesDescription),
                        icon = { ExpressiveRowIcon(rememberVectorPainter(Icons.Rounded.Schedule)) },
                        checked = autoCheckUpdatesPref.value,
                        onCheckedChange = { autoCheckUpdatesPref.value = it }
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        ExpressiveSection(
            title = sharedStringResource(SharedString.SettingsAboutIssues)
        ) {
            VerticalSegmentor(
                {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 8.dp, horizontal = 18.dp
                            )
                    ) {
                        ExpressiveRowHeader(
                            title = sharedStringResource(SharedString.SettingsAboutIssuesTitle),
                            description = sharedStringResource(SharedString.SettingsAboutIssuesDescription),
                            icon = {
                                ExpressiveRowIcon(
                                    painter = rememberVectorPainter(Icons.AutoMirrored.Rounded.ContactSupport)
                                )
                            }
                        )

                        ScrollableRow(
                            gradientColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                            modifier = Modifier.padding(top = 8.dp).align(Alignment.End)
                        ) {
                            supportLinks.forEach { social ->
                                OutlinedButton(
                                    onClick = { uriHandler.openUri(social.url) },
                                    shapes = ButtonDefaults.shapes()
                                ) {
                                    ButtonIcon(social.getIconPainter())
                                    Text(social.label.resolveString())
                                }
                            }
                        }
                    }
                },
                {
                    ExpressiveButtonRow(
                        title = sharedStringResource(SharedString.SettingsAboutIssuesCopyDebugInfo),
                        description = sharedStringResource(SharedString.SettingsAboutIssuesCopyDebugInfoDescription),
                        icon = { ExpressiveRowIcon(rememberVectorPainter(Icons.Rounded.CopyAll)) }
                    ) {
                        scope.launch {
                            clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(
                                context.getSharedString(SharedString.SettingsAboutIssuesCopyDebugInfoClipLabel),
                                debugInfo
                            )))
                            vm.topToastState.showToast(
                                text = context.getSharedString(SharedString.SettingsAboutIssuesCopyDebugInfoCopied),
                                icon = Icons.Rounded.CopyAll
                            )
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        ExpressiveSection(
            title = sharedStringResource(SharedString.SettingsAboutCredits)
        ) {
            LaunchedEffect(Unit) {
                credits.forEach {
                    it.fetchAvatar()
                }
            }

            val creditsButtons: List<@Composable () -> Unit> = credits.map { credit -> {
                ExpressiveButtonRow(
                    title = credit.name.resolveString(),
                    description = credit.description.resolveString(),
                    icon = credit.avatarURL?.let { {
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            modifier = Modifier
                                .size(ROW_DEFAULT_ICON_SIZE)
                                .clip(CircleShape)
                        )
                    } } ?: {
                        ExpressiveRowIcon(
                            painter = rememberVectorPainter(Icons.Rounded.Face)
                        )
                    }
                ) {
                    credit.link?.let { uriHandler.openUri(it) }
                }
            } }

            VerticalSegmentor(
                *creditsButtons.toTypedArray(),
                {
                    ExpressiveButtonRow(
                        title = sharedStringResource(SharedString.SettingsAboutLibs),
                        description = sharedStringResource(SharedString.SettingsAboutLibsDescription),
                        icon = { ExpressiveRowIcon(rememberVectorPainter(Icons.Rounded.Book)) },
                        trailingComponent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        },
                        onClick = onNavigateLibsRequest
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ChangelogButton(
    updateAvailable: Boolean,
    onClick: () -> Unit
) {
    AnimatedContent(updateAvailable) {
        if (it) ElevatedButton(
            shapes = ButtonDefaults.shapes(),
            onClick = onClick
        ) {
            ButtonIcon(
                rememberVectorPainter(Icons.Default.Update)
            )
            Text(sharedStringResource(SharedString.SettingsAboutUpdate))
        }
        else FilledTonalButton(
            shapes = ButtonDefaults.shapes(),
            onClick = onClick
        ) {
            ButtonIcon(
                rememberVectorPainter(Icons.Default.Description)
            )
            Text(sharedStringResource(SharedString.SettingsAboutChangelog))
        }
    }
}