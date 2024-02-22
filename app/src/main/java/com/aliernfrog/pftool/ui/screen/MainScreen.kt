package com.aliernfrog.pftool.ui.screen

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import com.aliernfrog.pftool.ui.dialog.ProgressDialog
import com.aliernfrog.pftool.ui.screen.maps.MapsPermissionsScreen
import com.aliernfrog.pftool.ui.sheet.UpdateSheet
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.pftool.util.Destination
import com.aliernfrog.pftool.util.NavigationConstant
import com.aliernfrog.pftool.util.extension.popBackStackSafe
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    BaseScaffold(
        navController = navController
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavigationConstant.INITIAL_DESTINATION,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding(),
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
                MapsPermissionsScreen(
                    onNavigateSettingsRequest = {
                        navController.navigate(Destination.SETTINGS.route)
                    }
                )
            }
            composable(route = Destination.SETTINGS.route) {
                SettingsScreen(
                    onNavigateBackRequest = {
                        navController.popBackStackSafe()
                    }
                )
            }
        }
    }

    UpdateSheet(
        sheetState = mainViewModel.updateSheetState,
        latestVersionInfo = mainViewModel.latestVersionInfo
    )

    mainViewModel.progressState.currentProgress?.let {
        ProgressDialog(it) {}
    }
}