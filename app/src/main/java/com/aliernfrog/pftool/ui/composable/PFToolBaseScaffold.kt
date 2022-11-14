package com.aliernfrog.pftool.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.Screen
import com.aliernfrog.pftool.getScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PFToolBaseScaffold(navController: NavController, content: @Composable (PaddingValues) -> Unit) {
    val screens = getScreens()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val currentScreen = screens.find { it.route == currentRoute }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).imePadding(),
        topBar = { TopBar(navController, scrollBehavior, currentScreen) },
        bottomBar = { BottomBar(navController, screens, currentScreen) }
    ) {
        content(it)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(navController: NavController, scrollBehavior: TopAppBarScrollBehavior, currentScreen: Screen?) {
    val context = LocalContext.current
    LargeTopAppBar(
        title = { Text(text = currentScreen?.name ?: context.getString(R.string.app_name), fontWeight = FontWeight.SemiBold) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            AnimatedVisibility(visible = navController.previousBackStackEntry != null) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = context.getString(R.string.action_back),
                    modifier = Modifier.padding(horizontal = 8.dp).clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false),
                        onClick = { navController.navigateUp() }
                    )
                )
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BottomBar(navController: NavController, screens: List<Screen>, currentScreen: Screen?) {
    AnimatedVisibility(
        visible = !WindowInsets.isImeVisible,
        enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(durationMillis = 100)) + fadeIn(),
        exit = fadeOut(animationSpec = tween(durationMillis = 0))
    ) {
        BottomAppBar {
            screens.filter { it.showInNavigationBar }.forEach {
                NavigationBarItem(
                    selected = it.route == currentScreen?.route,
                    onClick = { navController.navigate(it.route) { popUpTo(0) } },
                    icon = { Image(it.icon, it.name, colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface), modifier = Modifier.size(28.dp)) },
                    label = { Text(it.name, modifier = Modifier.offset(y = 5.dp)) }
                )
            }
        }
    }
}