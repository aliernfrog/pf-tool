package com.aliernfrog.pftool.ui.component

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.enum.NavigationStyle
import com.aliernfrog.pftool.enum.getNavigationStyle
import com.aliernfrog.pftool.util.Destination

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun BaseScaffold(navController: NavController, content: @Composable (PaddingValues) -> Unit) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val navigationStyle = getNavigationStyle(windowSizeClass)
    val showBottomBar = navigationStyle == NavigationStyle.NAVIGATION_BAR
    var sideBarWidth by remember { mutableStateOf(0.dp) }

    val destinations = remember { Destination.values().toList() }
    val mainDestinations = remember { destinations.filter { !it.isSubScreen } }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val currentDestination = destinations.find { it.route == currentRoute }

    fun changeDestination(destination: Destination) {
        navController.navigate(destination.route) { popUpTo(0) }
    }

    fun toDp(pxs: Int): Dp {
        with(density) {
            return pxs.toDp()
        }
    }

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                start = if (showBottomBar) 0.dp else sideBarWidth
            ),
        bottomBar = {
            if (showBottomBar) BottomBar(
                destinations = mainDestinations,
                currentDestination = currentDestination,
                onNavigateRequest = { changeDestination(it) }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        val paddingValues = if (showBottomBar || currentDestination?.isSubScreen != true) it
        else PaddingValues(
            start = it.calculateStartPadding(layoutDirection),
            top = it.calculateTopPadding(),
            end = it.calculateEndPadding(layoutDirection),
            bottom = 0.dp
        )
        content(paddingValues)
    }

    when (navigationStyle) {
        NavigationStyle.NAVIGATION_RAIL -> SideBarRail(
            destinations = mainDestinations,
            currentDestination = currentDestination,
            onWidthChange = { sideBarWidth = toDp(it) },
            onNavigateRequest = { changeDestination(it) }
        )
        NavigationStyle.NAVIGATION_DRAWER -> SideBarDrawer(
            destinations = mainDestinations,
            currentDestination = currentDestination,
            onWidthChange = { sideBarWidth = toDp(it) },
            onNavigateRequest = { changeDestination(it) }
        )
        else -> {}
    }

    LaunchedEffect(currentDestination) {
        if (currentDestination?.hasNotification?.value == true)
            currentDestination.hasNotification.value = false
    }
}

@Composable
private fun BottomBar(
    destinations: List<Destination>,
    currentDestination: Destination?,
    onNavigateRequest: (Destination) -> Unit
) {
    AnimatedVisibility(
        visible = !(currentDestination?.isSubScreen ?: false),
        enter = slideInVertically(animationSpec = tween(durationMillis = 150), initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(animationSpec = tween(durationMillis = 150), targetOffsetY = { it }) + fadeOut()
    ) {
        BottomAppBar {
            destinations.forEach {
                val selected = it.route == currentDestination?.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected && currentDestination?.isSubScreen != true)
                            onNavigateRequest(it)
                    },
                    icon = {
                        NavigationItemIcon(
                            destination = it,
                            selected = selected
                        )
                    },
                    label = {
                        Text(stringResource(it.labelId))
                    }
                )
            }
        }
    }
}

@Composable
private fun SideBarRail(
    destinations: List<Destination>,
    currentDestination: Destination?,
    onWidthChange: (Int) -> Unit,
    onNavigateRequest: (Destination) -> Unit
) {
    NavigationRail(
        modifier = Modifier
            .onSizeChanged { onWidthChange(it.width) }
    ) {
        AppIcon()
        destinations.filter { !it.isSubScreen }.forEach {
            val selected = it.route == currentDestination?.route
            NavigationRailItem(
                selected = selected,
                onClick = {
                    if (!selected && currentDestination?.isSubScreen != true)
                        onNavigateRequest(it)
                },
                icon = {
                    NavigationItemIcon(
                        destination = it,
                        selected = selected
                    )
                },
                label = {
                    Text(stringResource(it.labelId))
                }
            )
        }
    }
}

@Composable
private fun SideBarDrawer(
    destinations: List<Destination>,
    currentDestination: Destination?,
    onWidthChange: (Int) -> Unit,
    onNavigateRequest: (Destination) -> Unit
) {
    PermanentNavigationDrawer(
        drawerContent = {
            Column(
                Modifier.padding(horizontal = 12.dp)
            ) {
                AppIcon(showAppName = true)
                destinations.forEach {
                    val selected = it.route == currentDestination?.route
                    NavigationDrawerItem(
                        selected = selected,
                        onClick = {
                            if (!selected && currentDestination?.isSubScreen != true)
                                onNavigateRequest(it)
                        },
                        icon = { NavigationItemIcon(
                            destination = it,
                            selected = selected
                        ) },
                        label = {
                            Text(stringResource(it.labelId))
                        }
                    )
                }
            }
        },
        content = {},
        modifier = Modifier
            .widthIn(max = 360.dp)
            .onSizeChanged { onWidthChange(it.width) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationItemIcon(
    destination: Destination,
    selected: Boolean
) {
    @Composable fun ItemIcon() {
        Icon(
            imageVector = if (selected) destination.vectorFilled!! else destination.vectorOutlined!!,
            contentDescription = null
        )
    }

    if (destination.hasNotification.value) BadgedBox(
        badge = { Badge() }
    ) {
        ItemIcon()
    }
    else ItemIcon()
}

@Composable
private fun AppIcon(
    showAppName: Boolean = false
) {
    val context = LocalContext.current
    val appName = stringResource(R.string.app_name)
    val appIcon = remember {
        context.packageManager.getApplicationIcon(context.packageName)
            .toBitmap().asImageBitmap()
    }
    Row(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .height(64.dp)
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = appIcon,
            contentDescription = appName,
            modifier = Modifier
                .fillMaxHeight()
                .clip(CircleShape)
        )
        if (showAppName) Text(
            text = appName,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}