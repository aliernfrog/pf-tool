package com.aliernfrog.pftool.ui.component

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    topBar: @Composable (scrollBehavior: TopAppBarScrollBehavior) -> Unit,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior = adaptiveExitUntilCollapsedScrollBehavior(topAppBarState),
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
    navigationIcon: ImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
    onNavigationClick: (() -> Unit)? = null
) {
    val disableLargeTopAppBar = shouldDisableLargeTopAppBar()

    LaunchedEffect(disableLargeTopAppBar) {
        if (disableLargeTopAppBar) scrollBehavior.state.heightOffset = 0f
    }

    if (disableLargeTopAppBar && scrollBehavior.state.heightOffset == 0f) AppSmallTopBar(
        title = title,
        scrollBehavior = scrollBehavior,
        actions = actions,
        colors = colors,
        navigationIcon = navigationIcon,
        onNavigationClick = onNavigationClick
    ) else LargeTopAppBar(
        title = { Text(title) },
        scrollBehavior = scrollBehavior,
        colors = colors,
        navigationIcon = {
            onNavigationClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            }
        },
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSmallTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
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
                IconButton(onClick = it) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            }
        },
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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