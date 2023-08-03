package com.aliernfrog.pftool.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.pftool.ui.component.BaseScaffold
import com.aliernfrog.pftool.ui.screen.MapsScreen
import com.aliernfrog.pftool.ui.screen.PermissionsScreen
import com.aliernfrog.pftool.ui.screen.SettingsScreen
import com.aliernfrog.pftool.ui.sheet.PickMapSheet
import com.aliernfrog.pftool.ui.sheet.UpdateSheet
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.aliernfrog.pftool.ui.theme.Theme
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import com.aliernfrog.pftool.util.Destination
import com.aliernfrog.pftool.util.NavigationConstant
import com.aliernfrog.pftool.util.getScreens
import com.aliernfrog.toptoast.component.TopToastHost
import org.koin.androidx.viewmodel.ext.android.getViewModel

@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppContent()
        }
    }

    @Composable
    private fun AppContent(
        mainViewModel: MainViewModel = getViewModel()
    ) {
        val view = LocalView.current
        val scope = rememberCoroutineScope()
        val useDarkTheme = shouldUseDarkTheme(mainViewModel.prefs.theme)
        PFToolTheme(
            darkTheme = useDarkTheme,
            dynamicColors = mainViewModel.prefs.materialYou
        ) {
            BaseScaffold()
            TopToastHost(mainViewModel.topToastState)
        }

        LaunchedEffect(Unit) {
            mainViewModel.scope = scope
            mainViewModel.topToastState.setComposeView(view)

            if (mainViewModel.prefs.autoCheckUpdates) mainViewModel.checkUpdates()
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun BaseScaffold(
        mainViewModel: MainViewModel = getViewModel(),
        mapsViewModel: MapsViewModel = getViewModel()
    ) {
        val navController = rememberNavController()
        val screens = getScreens()
        BaseScaffold(screens, navController) {
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
        PickMapSheet(
            sheetState = mapsViewModel.pickMapSheetState,
            onMapPick = {
                mapsViewModel.chooseMap(it)
                true
            }
        )
        UpdateSheet(
            sheetState = mainViewModel.updateSheetState,
            latestVersionInfo = mainViewModel.latestVersionInfo
        )
    }

    @Composable
    private fun shouldUseDarkTheme(theme: Int): Boolean {
        return when(theme) {
            Theme.LIGHT.int -> false
            Theme.DARK.int -> true
            else -> isSystemInDarkTheme()
        }
    }
}