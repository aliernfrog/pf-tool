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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.screen.MapsScreenRoot
import com.aliernfrog.pftool.ui.screen.OptionsScreen
import com.aliernfrog.pftool.ui.sheet.DeleteMapSheet
import com.aliernfrog.pftool.ui.sheet.PickMapSheet
import com.aliernfrog.pftool.ui.state.MapsState
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.aliernfrog.toptoast.TopToastBase
import com.aliernfrog.toptoast.TopToastManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var config: SharedPreferences
    private lateinit var configEditor: SharedPreferences.Editor
    private lateinit var topToastManager: TopToastManager
    private lateinit var pickMapSheetState: ModalBottomSheetState
    private lateinit var deleteMapSheetState: ModalBottomSheetState
    private lateinit var mapsState: MapsState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        config = getSharedPreferences(ConfigKey.PREF_NAME, MODE_PRIVATE)
        configEditor = config.edit()
        topToastManager = TopToastManager()
        pickMapSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        deleteMapSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, isSkipHalfExpanded = true)
        mapsState = MapsState(topToastManager, config, pickMapSheetState, deleteMapSheetState)
        setContent {
            val darkTheme = getDarkThemePreference()
            PFToolTheme(darkTheme, getDynamicColorsPreference()) {
                TopToastBase(backgroundColor = MaterialTheme.colorScheme.background, manager = topToastManager, content = { BaseScaffold() })
                SystemBars(darkTheme)
            }
        }
    }

    @Composable
    private fun BaseScaffold() {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()
        PFToolBaseScaffold(navController) {
            NavHost(navController = navController, startDestination = NavRoutes.MAPS, Modifier.fillMaxSize().padding(it).verticalScroll(rememberScrollState())) {
                composable(route = NavRoutes.MAPS) {
                    MapsScreenRoot(mapsState)
                }
                composable(route = NavRoutes.OPTIONS) {
                    OptionsScreen(topToastManager, config)
                }
            }
        }
        PickMapSheet(
            mapsState = mapsState,
            topToastManager = topToastManager,
            sheetState = pickMapSheetState,
            showMapThumbnails = remember { config.getBoolean(ConfigKey.KEY_SHOW_MAP_THUMBNAILS_LIST, true) },
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
        controller.statusBarDarkContentEnabled = !darkTheme
    }

    @Composable
    private fun getDarkThemePreference(): Boolean {
        return when(config.getInt(ConfigKey.KEY_APP_THEME, Theme.SYSTEM)) {
            Theme.LIGHT -> false
            Theme.DARK -> true
            else -> isSystemInDarkTheme()
        }
    }

    private fun getDynamicColorsPreference(): Boolean {
        return config.getBoolean(ConfigKey.KEY_APP_MATERIAL_YOU, true)
    }
}