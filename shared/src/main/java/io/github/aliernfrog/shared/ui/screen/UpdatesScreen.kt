package io.github.aliernfrog.shared.ui.screen

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import io.github.aliernfrog.shared.data.ReleaseInfo
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppSmallTopBar
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.CardWithActions
import io.github.aliernfrog.shared.ui.component.ErrorWithIcon
import io.github.aliernfrog.shared.ui.component.IconButtonWithTooltip
import io.github.aliernfrog.shared.ui.component.TextWithIcon
import io.github.aliernfrog.shared.ui.component.util.LazyListScrollAccessibilityListener
import io.github.aliernfrog.shared.ui.component.verticalSegmentedShape
import io.github.aliernfrog.shared.ui.theme.AppComponentShape
import io.github.aliernfrog.shared.ui.theme.AppFABPadding
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.sdkVersionToAndroidVersion
import io.github.aliernfrog.shared.util.sharedStringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UpdatesScreen(
    availableUpdates: List<ReleaseInfo>,
    currentVersionInfo: ReleaseInfo,
    isCheckingForUpdates: Boolean,
    isCompatibleWithLatestVersion: Boolean,
    onCheckUpdatesRequest: () -> Unit,
    onNavigateBackRequest: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val uriHandler = LocalUriHandler.current
    val updateAvailable = availableUpdates.isNotEmpty()
    var showExtendedToolbar by remember { mutableStateOf(true) }

    LazyListScrollAccessibilityListener(
        lazyListState = lazyListState
    ) {
        showExtendedToolbar = it
    }

    AppScaffold(
        topBar = { scrollBehavior ->
            AppSmallTopBar(
                title = sharedStringResource(
                    if (updateAvailable) SharedString.Updates
                    else SharedString.UpdatesChangelog
                ),
                scrollBehavior = scrollBehavior,
                onNavigationClick = onNavigateBackRequest
            )
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ) {
        PullToRefreshBox(
            isRefreshing = isCheckingForUpdates,
            onRefresh = onCheckUpdatesRequest
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize()
            ) {
                if (!updateAvailable && currentVersionInfo.body == null) item {
                    ErrorWithIcon(
                        error = sharedStringResource(SharedString.UpdatesNoChangelog),
                        painter = rememberVectorPainter(Icons.AutoMirrored.Filled.Notes),
                        button = {
                            Button(
                                onClick = onCheckUpdatesRequest,
                                shapes = ButtonDefaults.shapes()
                            ) {
                                ButtonIcon(rememberVectorPainter(Icons.Default.Refresh))
                                Text(sharedStringResource(SharedString.UpdatesCheckUpdates))
                            }
                        }
                    )
                }

                if (!isCompatibleWithLatestVersion) item {
                    val minAndroidVersion = remember(availableUpdates) {
                        sdkVersionToAndroidVersion(availableUpdates.firstOrNull()?.minSdk ?: 0)
                    }

                    CardWithActions(
                        title = sharedStringResource(SharedString.Warning),
                        icon = rememberVectorPainter(Icons.Default.Warning),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        buttons = null,
                        modifier = Modifier
                            .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                sharedStringResource(SharedString.UpdatesIncompatible)
                                    .replace("{ANDROID_VERSION}", minAndroidVersion)
                            )
                        }
                    }
                }

                itemsIndexed(availableUpdates) { index, release ->
                    ReleaseCard(
                        release = release,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .verticalSegmentedShape(index, totalSize = availableUpdates.size)
                    )
                }

                if (currentVersionInfo.body != null) item {
                    ReleaseCard(
                        release = currentVersionInfo,
                        isCurrentRelease = true,
                        modifier = Modifier
                            .padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            )
                            .clip(AppComponentShape)
                    )
                }

                item {
                    Spacer(Modifier.padding(top = AppFABPadding).navigationBarsPadding())
                }
            }

            HorizontalFloatingToolbar(
                expanded = true,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            uriHandler.openUri(currentVersionInfo.htmlUrl)
                        },
                        shapes = ButtonDefaults.shapes()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        AnimatedVisibility(showExtendedToolbar) {
                            Text(
                                text = sharedStringResource(SharedString.ActionOpenInBrowser),
                                modifier = Modifier.padding(start = ButtonDefaults.IconSpacing)
                            )
                        }
                    }

                    AnimatedContent(
                        targetState = updateAvailable
                    ) { showUpdate ->
                        if (showUpdate) Button(
                            onClick = { uriHandler.openUri(currentVersionInfo.downloadUrl) },
                            shapes = ButtonDefaults.shapes()
                        ) {
                            ButtonIcon(rememberVectorPainter(Icons.Default.Update))
                            Text(sharedStringResource(SharedString.UpdatesUpdate))
                        }
                        else FilledTonalButton(
                            onClick = onCheckUpdatesRequest,
                            shapes = ButtonDefaults.shapes()
                        ) {
                            ButtonIcon(rememberVectorPainter(Icons.Default.Refresh))
                            Text(sharedStringResource(SharedString.UpdatesCheckUpdates))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ReleaseCard(
    release: ReleaseInfo,
    isCurrentRelease: Boolean = false,
    modifier: Modifier
) {
    val uriHandler = LocalUriHandler.current
    val releasedAtText = remember(release.createdAt) {
        DateUtils.getRelativeTimeSpanString(
            /* time = */ release.createdAt,
            /* now = */ System.currentTimeMillis(),
            /* minResolution = */ DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                Text(
                    text = release.versionName,
                    style = MaterialTheme.typography.titleLargeEmphasized,
                )

                if (release.prerelease) Text(
                    text = sharedStringResource(SharedString.UpdatesPrerelease).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = 4.dp)
                )
            }

            IconButtonWithTooltip(
                icon = rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew),
                contentDescription = sharedStringResource(SharedString.ActionOpenInBrowser),
                onClick = { uriHandler.openUri(release.htmlUrl) }
            )
        }

        Column(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (isCurrentRelease) TextWithIcon(
                    text = sharedStringResource(SharedString.UpdatesCurrentVersion),
                    icon = rememberVectorPainter(Icons.Default.DownloadDone)
                )

                TextWithIcon(
                    text = releasedAtText,
                    icon = rememberVectorPainter(Icons.Default.DateRange)
                )
            }

            MarkdownText(
                markdown = release.body.toString(),
                linkColor = MaterialTheme.colorScheme.primary,
                syntaxHighlightColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                syntaxHighlightTextColor = MaterialTheme.colorScheme.onSurface,
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current
                ),
                onLinkClicked = {
                    uriHandler.openUri(it)
                }
            )
        }
    }
}