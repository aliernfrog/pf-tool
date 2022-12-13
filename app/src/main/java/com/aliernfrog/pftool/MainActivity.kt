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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.aliernfrog.pftool.ui.sheet.DeleteMapSheet
import com.aliernfrog.pftool.ui.sheet.PickMapSheet
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.aliernfrog.pftool.ui.theme.Theme
import com.aliernfrog.pftool.util.Destination
import com.aliernfrog.pftool.util.NavigationConstant
import com.aliernfrog.pftool.util.getScreens
import com.aliernfrog.toptoast.component.TopToastHost
import com.aliernfrog.toptoast.state.TopToastState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var config: SharedPreferences
    private lateinit var topToastState: TopToastState
    private lateinit var optionsState: OptionsState
    private lateinit var pickMapSheetState: ModalBottomSheetState
    private lateinit var deleteMapSheetState: ModalBottomSheetState
    private lateinit var mapsState: MapsState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        config = getSharedPreferences(ConfigKey.PREF_NAME, MODE_PRIVATE)
        topToastState = TopToastState()
        optionsState = OptionsState(config)
        pickMapSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        deleteMapSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, isSkipHalfExpanded = true)
        mapsState = MapsState(topToastState, config, pickMapSheetState, deleteMapSheetState)
        setContent {
            val darkTheme = getDarkThemePreference()
            PFToolTheme(darkTheme, optionsState.materialYou.value) {
                TopToastHost(
                    state = topToastState,
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                ) { BaseScaffold() }
                SystemBars(darkTheme)
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun BaseScaffold() {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
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
            PFToolSheetBackHandler(pickMapSheetState, deleteMapSheetState)
        }
        PickMapSheet(
            mapsState = mapsState,
            topToastState = topToastState,
            sheetState = pickMapSheetState,
            showMapThumbnails = optionsState.showMapThumbnailsInList.value,
            onFilePick = { mapsState.getMap(file = it, context = context) },
            onDocumentFilePick = { mapsState.getMap(documentFile = it, context = context) }
        )
        DeleteMapSheet(
            mapName = mapsState.lastMapName.value,
            sheetState = deleteMapSheetState
        ) {
            scope.launch { mapsState.deleteChosenMap(context) }
        }
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