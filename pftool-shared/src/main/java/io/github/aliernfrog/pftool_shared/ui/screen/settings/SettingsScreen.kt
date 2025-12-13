package io.github.aliernfrog.pftool_shared.ui.screen.settings

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
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.pftool_shared.data.ReleaseInfo
import io.github.aliernfrog.pftool_shared.ui.component.AppScaffold
import io.github.aliernfrog.pftool_shared.ui.component.AppSmallTopBar
import io.github.aliernfrog.pftool_shared.ui.component.AppTopBar
import io.github.aliernfrog.pftool_shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.pftool_shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.pftool_shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.pftool_shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.pftool_shared.ui.component.expressive.toRowFriendlyColor
import io.github.aliernfrog.pftool_shared.ui.theme.AppComponentShape
import io.github.aliernfrog.pftool_shared.util.SharedString
import io.github.aliernfrog.pftool_shared.util.sharedStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRootPage(
    categories: List<SettingsCategory>,
    updateAvailable: Boolean,
    latestReleaseInfo: ReleaseInfo,
    onShowUpdateSheetRequest: () -> Unit,
    onNavigateBackRequest: () -> Unit,
    onNavigateRequest: (SettingsDestination) -> Unit
) {
    AppScaffold(
        topBar = { scrollBehavior ->
            AppTopBar(
                title = sharedStringResource(SharedString.SETTINGS),
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
                isShown = updateAvailable,
                versionInfo = latestReleaseInfo,
                onClick = onShowUpdateSheetRequest
            )

            categories.forEach { category ->
                ExpressiveSection(
                    title = category.title
                ) {
                    val buttons: List<@Composable () -> Unit> = category.destinations.map { destination -> {
                        ExpressiveButtonRow(
                            title = destination.title,
                            description = destination.description,
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
    isShown: Boolean,
    versionInfo: ReleaseInfo,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isShown,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        ExpressiveButtonRow(
            title = sharedStringResource(SharedString.SETTINGS_UPDATE_NOTIFICATION_UPDATE_AVAILABLE)
                .replace("{VERSION}", versionInfo.versionName),
            description = sharedStringResource(SharedString.SETTINGS_UPDATE_NOTIFICATION_DESCRIPTION),
            icon = { ExpressiveRowIcon(rememberVectorPainter(Icons.Rounded.Update)) },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(12.dp).clip(AppComponentShape),
            onClick = onClick
        )
    }
}

data class SettingsDestination(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconContainerColor: Color = Color.Blue,
    val shown: Boolean = true,
    val content: @Composable (
        onNavigateBackRequest: () -> Unit,
        onNavigateRequest: (SettingsDestination) -> Unit
    ) -> Unit
)

data class SettingsCategory(
    val title: String,
    val destinations: List<SettingsDestination>
)