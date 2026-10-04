package io.github.aliernfrog.shared.ui.component

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.blur.material3.Material3
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.sharedStringResource

@Composable
fun AppScaffold(
    topBar: @Composable (scrollBehavior: TopAppBarScrollBehavior) -> Unit,
    modifier: Modifier = Modifier,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior = adaptiveExitUntilCollapsedScrollBehavior(topAppBarState),
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { topBar(scrollBehavior) },
        floatingActionButton = floatingActionButton,
        contentWindowInsets = WindowInsets(0,0,0,0),
        content = {
            Box(modifier = Modifier.padding(it)) {
                content()
            }
        }
    )
}

@Composable
fun AppScaffoldNoContentPadding(
    topBar: @Composable (scrollBehavior: TopAppBarScrollBehavior) -> Unit,
    modifier: Modifier = Modifier,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior = adaptiveExitUntilCollapsedScrollBehavior(topAppBarState),
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { topBar(scrollBehavior) },
        floatingActionButton = floatingActionButton,
        contentWindowInsets = WindowInsets(0,0,0,0),
        content = {
            content(it)
        }
    )
}

@Composable
fun AppTopBar(
    title: String,
    hazeState: HazeState,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    navigationIcon: ImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
    onNavigationClick: (() -> Unit)? = null
) {
    val disableLargeTopAppBar = shouldDisableLargeTopAppBar()

    LaunchedEffect(disableLargeTopAppBar) {
        if (disableLargeTopAppBar) scrollBehavior.state.heightOffset = 0f
    }

    if (disableLargeTopAppBar && scrollBehavior.state.heightOffset == 0f) AppSmallTopBarWithBlur(
        title = title,
        hazeState = hazeState,
        scrollBehavior = scrollBehavior,
        actions = actions,
        colors = colors, // scrolledContainerColor transparency is automatically handled here
        navigationIcon = navigationIcon,
        onNavigationClick = onNavigationClick,
        modifier = modifier
    ) else LargeFlexibleTopAppBar(
        title = { Text(title) },
        scrollBehavior = scrollBehavior,
        colors = colors.let {
            it.copy(
                scrolledContainerColor = it.scrolledContainerColor.copy(alpha = 0.7f)
            )
        },
        navigationIcon = {
            onNavigationClick?.let {
                BackButtonWithTooltip(
                    icon = navigationIcon,
                    onClick = it
                )
            }
        },
        actions = actions,
        modifier = modifier.hazeBlur(
            input = HazeInput.Backdrop(hazeState),
            style = HazeBlurStyle.Material3(
                containerColor = colors.scrolledContainerColor
            )
        )
    )
}

@Composable
fun AppSmallTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    navigationIcon: ImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
    onNavigationClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title) },
        scrollBehavior = scrollBehavior,
        colors = colors,
        navigationIcon = {
            onNavigationClick?.let {
                BackButtonWithTooltip(
                    icon = navigationIcon,
                    onClick = it
                )
            }
        },
        actions = actions,
        modifier = modifier
    )
}

@Composable
fun AppSmallTopBarWithBlur(
    title: String,
    hazeState: HazeState,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    navigationIcon: ImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
    onNavigationClick: (() -> Unit)? = null
) {
    AppSmallTopBar(
        title = title,
        scrollBehavior = scrollBehavior,
        modifier = modifier.hazeBlur(
            input = HazeInput.Backdrop(hazeState),
            style = HazeBlurStyle.Material3(
                containerColor = colors.scrolledContainerColor
            )
        ),
        actions = actions,
        colors = colors.let {
            it.copy(
                scrolledContainerColor = it.scrolledContainerColor.copy(alpha = 0.7f)
            )
        },
        navigationIcon = navigationIcon,
        onNavigationClick = onNavigationClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BackButtonWithTooltip(icon: ImageVector, onClick: () -> Unit) {
    IconButtonWithTooltip(
        icon = rememberVectorPainter(icon),
        contentDescription = sharedStringResource(SharedString::actionBack),
        tooltipPositioning = TooltipAnchorPosition.Below,
        onClick = onClick
    )
}

@Composable
fun adaptiveExitUntilCollapsedScrollBehavior(
    topAppBarState: TopAppBarState = rememberTopAppBarState()
): TopAppBarScrollBehavior {
    return if (shouldDisableLargeTopAppBar()) TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    else TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun shouldDisableLargeTopAppBar(): Boolean {
    val context = LocalContext.current
    val heightSizeClass = calculateWindowSizeClass(context as Activity)
    return heightSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
}