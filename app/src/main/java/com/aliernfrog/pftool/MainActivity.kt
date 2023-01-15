package com.aliernfrog.pftool

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.pftool.state.MapsState
import com.aliernfrog.pftool.state.OptionsState
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.composable.PFToolSheetBackHandler
import com.aliernfrog.pftool.ui.screen.MapsScreen
import com.aliernfrog.pftool.ui.screen.OptionsScreen
import com.aliernfrog.pftool.ui.screen.PermissionsScreen
import com.aliernfrog.pftool.ui.sheet.PickMapSheet
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.aliernfrog.pftool.ui.theme.Theme
import com.aliernfrog.pftool.util.Destination
import com.aliernfrog.pftool.util.NavigationConstant
import com.aliernfrog.pftool.util.getScreens
import com.aliernfrog.toptoast.component.TopToastHost
import com.aliernfrog.toptoast.state.TopToastState
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var config: SharedPreferences
    private lateinit var topToastState: TopToastState
    private lateinit var optionsState: OptionsState
    private lateinit var pickMapSheetState: ModalBottomSheetState
    private lateinit var mapsState: MapsState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        config = getSharedPreferences(ConfigKey.PREF_NAME, MODE_PRIVATE)
        topToastState = TopToastState()
        optionsState = OptionsState(config)
        pickMapSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        mapsState = MapsState(topToastState, config, pickMapSheetState)
        setContent {
            val darkTheme = getDarkThemePreference()
            PFToolTheme(darkTheme, optionsState.materialYou.value) {
                BaseScaffold()
                TopToastHost(topToastState)
                SystemBars(darkTheme)
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun BaseScaffold() {
        val navController = rememberNavController()
        val screens = getScreens(navController)
        PFToolBaseScaffold(screens, navController) {
            NavHost(
                navController = navController,
                startDestination = NavigationConstant.INITIAL_DESTINATION,
                modifier = Modifier.fillMaxSize().padding(it).consumeWindowInsets(it).systemBarsPadding()
            ) {
                composable(route = Destination.MAPS.route) { PermissionsScreen(mapsState.mapsDir) { MapsScreen(mapsState) } }
                composable(route = Destination.OPTIONS.route) { OptionsScreen(config, topToastState, optionsState) }
            }
            PFToolSheetBackHandler(pickMapSheetState)
        }
        PickMapSheet(
            mapsState = mapsState,
            topToastState = topToastState,
            sheetState = pickMapSheetState,
            showMapThumbnails = optionsState.showMapThumbnailsInList.value,
            onFilePick = { mapsState.getMap(file = it) },
            onDocumentFilePick = { mapsState.getMap(documentFile = it) }
        )
    }

    @Composable
    private fun SystemBars(darkTheme: Boolean) {
        val controller = rememberSystemUiController()
        controller.systemBarsDarkContentEnabled = !darkTheme
        controller.isNavigationBarContrastEnforced = false
    }

    @Composable
    private fun getDarkThemePreference(): Boolean {
        return when(optionsState.theme.value) {
            Theme.LIGHT.int -> false
            Theme.DARK.int -> true
            else -> isSystemInDarkTheme()
        }
    }
}