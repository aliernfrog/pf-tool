package io.github.aliernfrog.shared.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.shared.data.ReleaseInfo
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppSmallTopBar
import io.github.aliernfrog.shared.ui.component.AppTopBar
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.ui.component.expressive.toRowFriendlyColor
import io.github.aliernfrog.shared.ui.theme.AppComponentShape
import io.github.aliernfrog.shared.ui.viewmodel.settings.SettingsViewModel
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.getSharedString
import io.github.aliernfrog.shared.util.sharedStringResource
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRootPage(
    categories: List<SettingsCategory>,
    availableUpdates: List<ReleaseInfo>,
    experimentalOptionsEnabled: Boolean,
    vm: SettingsViewModel = koinViewModel(),
    onShowUpdateSheetRequest: () -> Unit,
    onNavigateBackRequest: () -> Unit,
    onNavigateRequest: (SettingsDestination) -> Unit
) {
    val context = LocalContext.current

    AppScaffold(
        topBar = { scrollBehavior ->
            AppTopBar(
                title = sharedStringResource(SharedString.Settings),
                scrollBehavior = scrollBehavior,
                onNavigationClick = onNavigateBackRequest
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            UpdateNotification(
                availableUpdates = availableUpdates,
                onClick = onShowUpdateSheetRequest
            )

            categories.forEach { category ->
                ExpressiveSection(
                    title = sharedStringResource(category.title)
                ) {
                    val buttons: List<@Composable () -> Unit> = category.destinations
                        .filter {
                            it.shown() && (experimentalOptionsEnabled || it != SettingsDestination.experimental)
                        }
                        .map { destination -> {
                            val description = rememberSaveable {
                                destination.descriptionOverride?.let { it() }
                                    ?: context.getSharedString(destination.description)
                            }

                            ExpressiveButtonRow(
                                title = sharedStringResource(destination.title),
                                description = description,
                                icon = {
                                    ExpressiveRowIcon(
                                        painter = rememberVectorPainter(destination.icon),
                                        containerColor = destination.iconContainerColor.toRowFriendlyColor
                                    )
                                }
                            ) {
                                onNavigateRequest(destination)
                            }
                        } }

                    VerticalSegmentor(
                        *buttons.toTypedArray(),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Text(
                text = vm.versionLabel,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp, start = 12.dp , end = 12.dp)
                    .alpha(0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPageContainer(
    title: String,
    onNavigateBackRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    AppScaffold(
        topBar = { scrollBehavior ->
            AppSmallTopBar(
                title = title,
                scrollBehavior = scrollBehavior,
                onNavigationClick = onNavigateBackRequest
            )
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
            content = content
        )
    }
}

@Composable
private fun UpdateNotification(
    availableUpdates: List<ReleaseInfo>,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = availableUpdates.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        val latestUpdate = availableUpdates.firstOrNull()
        ExpressiveButtonRow(
            title = sharedStringResource(SharedString.SettingsUpdateNotificationUpdateAvailable)
                .replace("{VERSION}", latestUpdate?.versionName.toString()),
            description = sharedStringResource(SharedString.SettingsUpdateNotificationDescription),
            icon = { ExpressiveRowIcon(rememberVectorPainter(Icons.Rounded.Update)) },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(12.dp).clip(AppComponentShape),
            onClick = onClick
        )
    }
}

data class SettingsDestination(
    val title: SharedString,
    val description: SharedString,
    val icon: ImageVector,
    val iconContainerColor: Color,
    val shown: () -> Boolean = { true },
    val descriptionOverride: (() -> String)? = null
) {
    companion object {
        val root = SettingsDestination(
            title = SharedString.Settings,
            description = SharedString.Settings,
            icon = Icons.Rounded.Settings,
            iconContainerColor = Color.Blue,
            shown = { false }
        )

        val appearance = SettingsDestination(
            title = SharedString.SettingsAppearance,
            description = SharedString.SettingsAppearanceDescription,
            icon = Icons.Rounded.Palette,
            iconContainerColor = Color.Yellow
        )

        val experimental = SettingsDestination(
            title = SharedString.SettingsExperimental,
            description = SharedString.SettingsExperimentalDescription,
            icon = Icons.Rounded.Science,
            iconContainerColor = Color.Black
        )

        val about = SettingsDestination(
            title = SharedString.SettingsAbout,
            description = SharedString.SettingsAboutDescription,
            icon = Icons.Rounded.Info,
            iconContainerColor = Color.Blue
        )

        val libs = SettingsDestination(
            title = SharedString.SettingsAboutLibs,
            description = SharedString.SettingsAboutLibsDescription,
            icon = Icons.Rounded.Info,
            iconContainerColor = Color.Blue,
            shown = { false }
        )
    }
}

interface SettingsCategory {
    val title: SharedString
    val destinations: List<SettingsDestination>
}

class SettingsCategoryBuilder(override val title: SharedString) : SettingsCategory {
    override val destinations = mutableListOf<SettingsDestination>()

    operator fun SettingsDestination.unaryPlus() {
        destinations.add(this)
    }
}

fun category(
    title: SharedString,
    block: SettingsCategoryBuilder.() -> Unit
): SettingsCategory {
    return SettingsCategoryBuilder(title).apply(block)
}