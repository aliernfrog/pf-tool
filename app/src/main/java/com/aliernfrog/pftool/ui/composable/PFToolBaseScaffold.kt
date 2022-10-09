package com.aliernfrog.pftool.ui.composable

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aliernfrog.pftool.NavRoutes
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PFToolBaseScaffold(title: String, navController: NavController, onNavigationClick: (() -> Unit)? = null, content: @Composable (ColumnScope.() -> Unit)) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).imePadding(),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = LocalContext.current.getString(R.string.action_back), Modifier.padding(horizontal = 8.dp).clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false),
                            onClick = {
                                if (onNavigationClick != null) onNavigationClick()
                                navController.navigateUp()
                            })
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding)) {
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BottomBar(navController: NavController) {
    val context = LocalContext.current
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route
    val navScreens = listOf(
        Screen(NavRoutes.MAPS, context.getString(R.string.manageMaps), painterResource(id = R.drawable.map)),
        Screen(NavRoutes.OPTIONS, context.getString(R.string.options), painterResource(id = R.drawable.options))
    )
    AnimatedVisibility(
        visible = !WindowInsets.isImeVisible,
        enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(durationMillis = 100)) + fadeIn(),
        exit = fadeOut(animationSpec = tween(durationMillis = 0))
    ) {
        BottomAppBar {
            navScreens.forEach {
                NavigationBarItem(
                    selected = it.route == currentRoute,
                    onClick = { navController.navigate(it.route) { popUpTo(0) } },
                    icon = { Image(it.icon, it.name, colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface), modifier = Modifier.size(28.dp)) },
                    label = { Text(it.name, modifier = Modifier.offset(y = 5.dp)) }
                )
            }
        }
    }
}