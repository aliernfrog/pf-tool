package com.aliernfrog.pftool.ui.screen

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.pftool.ui.component.BaseScaffold
import com.aliernfrog.pftool.ui.sheet.UpdateSheet
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import com.aliernfrog.pftool.util.Destination
import com.aliernfrog.pftool.util.NavigationConstant
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = getViewModel(),
    mapsViewModel: MapsViewModel = getViewModel()
) {
    val navController = rememberNavController()
    BaseScaffold(
        navController = navController
    ) {
        NavHost(
            navController = navController,
            startDestination = NavigationConstant.INITIAL_DESTINATION,
            modifier = Modifier.fillMaxSize().padding(it).consumeWindowInsets(it).imePadding(),
            enterTransition = { scaleIn(
                animationSpec = tween(delayMillis = 100),
                initialScale = 0.95f
            ) + fadeIn(
                animationSpec = tween(delayMillis = 100)
            ) },
            exitTransition = { fadeOut(tween(100)) },
            popEnterTransition = { scaleIn(
                animationSpec = tween(delayMillis = 100),
                initialScale = 1.05f
            ) + fadeIn(
                animationSpec = tween(delayMillis = 100)
            ) },
            popExitTransition = { scaleOut(
                animationSpec = tween(100),
                targetScale = 0.95f
            ) + fadeOut(
                animationSpec = tween(100)
            ) }
        ) {
            composable(route = Destination.MAPS.route) {
                PermissionsScreen(mapsViewModel.mapsDir) {
                    MapsScreen()
                }
            }
            composable(route = Destination.SETTINGS.route) {
                SettingsScreen()
            }
        }
    }

    UpdateSheet(
        sheetState = mainViewModel.updateSheetState,
        latestVersionInfo = mainViewModel.latestVersionInfo
    )
}