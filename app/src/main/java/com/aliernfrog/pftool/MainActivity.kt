package com.aliernfrog.pftool

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.aliernfrog.pftool.state.MapsState
import com.aliernfrog.pftool.state.SettingsState
import com.aliernfrog.pftool.state.UpdateState
import com.aliernfrog.pftool.ui.component.BaseScaffold
import com.aliernfrog.pftool.ui.component.SheetBackHandler
import com.aliernfrog.pftool.ui.dialog.UpdateDialog
import com.aliernfrog.pftool.ui.screen.MapsScreen
import com.aliernfrog.pftool.ui.screen.PermissionsScreen
import com.aliernfrog.pftool.ui.screen.SettingsScreen
import com.aliernfrog.pftool.ui.sheet.PickMapSheet
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.aliernfrog.pftool.ui.theme.Theme
import com.aliernfrog.pftool.util.Destination
import com.aliernfrog.pftool.util.NavigationConstant
import com.aliernfrog.pftool.util.getScreens
import com.aliernfrog.toptoast.component.TopToastHost
import com.aliernfrog.toptoast.state.TopToastState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var config: SharedPreferences
    private lateinit var topToastState: TopToastState
    private lateinit var settingsState: SettingsState
    private lateinit var updateState: UpdateState
    private lateinit var pickMapSheetState: ModalBottomSheetState
    private lateinit var mapsState: MapsState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        config = getSharedPreferences(ConfigKey.PREF_NAME, MODE_PRIVATE)
        topToastState = TopToastState(window.decorView)
        settingsState = SettingsState(topToastState, config)
        updateState = UpdateState(topToastState, config, applicationContext)
        pickMapSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        mapsState = MapsState(topToastState, config, pickMapSheetState)
        setContent {
            val darkTheme = getDarkThemePreference()
            PFToolTheme(darkTheme, settingsState.materialYou.value) {
                BaseScaffold()
                TopToastHost(topToastState)
                SystemBars(darkTheme)
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class, ExperimentalAnimationApi::class)
    @Composable
    private fun BaseScaffold() {
        val navController = rememberAnimatedNavController()
        val screens = getScreens()
        BaseScaffold(screens, navController) {
            AnimatedNavHost(
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
                composable(route = Destination.MAPS.route) { PermissionsScreen(mapsState.mapsDir) { MapsScreen(mapsState) } }
                composable(route = Destination.SETTINGS.route) { SettingsScreen(config, updateState, settingsState) }
            }
            SheetBackHandler(pickMapSheetState)
        }
        PickMapSheet(
            mapsState = mapsState,
            topToastState = topToastState,
            sheetState = pickMapSheetState,
            showMapThumbnails = settingsState.showMapThumbnailsInList.value,
            onFilePick = { mapsState.getMap(file = it) },
            onDocumentFilePick = { mapsState.getMap(documentFile = it) }
        )
        UpdateDialog(updateState)
    }

    @Composable
    private fun SystemBars(darkTheme: Boolean) {
        val controller = rememberSystemUiController()
        controller.systemBarsDarkContentEnabled = !darkTheme
        controller.isNavigationBarContrastEnforced = false
    }

    @Composable
    private fun getDarkThemePreference(): Boolean {
        return when(settingsState.theme.value) {
            Theme.LIGHT.int -> false
            Theme.DARK.int -> true
            else -> isSystemInDarkTheme()
        }
    }
}