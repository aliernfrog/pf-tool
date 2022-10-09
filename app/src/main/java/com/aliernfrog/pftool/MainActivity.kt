package com.aliernfrog.pftool

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.pftool.ui.screen.MapsScreenRoot
import com.aliernfrog.pftool.ui.screen.OptionsScreen
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.aliernfrog.toptoast.TopToastBase
import com.aliernfrog.toptoast.TopToastManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    private lateinit var config: SharedPreferences
    private lateinit var configEditor: SharedPreferences.Editor
    private lateinit var topToastManager: TopToastManager

    private val defaultMapsDir = ConfigKey.DEFAULT_MAPS_DIR.replace("%STORAGE%", Environment.getExternalStorageDirectory().toString())
    private val defaultMapsExportDir = ConfigKey.DEFAULT_MAPS_EXPORT_DIR.replace("%DOCUMENTS%", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString())
    private var mapsDir = defaultMapsDir

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        config = getSharedPreferences(ConfigKey.PREF_NAME, MODE_PRIVATE)
        configEditor = config.edit()
        topToastManager = TopToastManager()
        mapsDir = config.getString(ConfigKey.KEY_MAPS_DIR, defaultMapsDir).toString()
        setConfig()
        setContent {
            val darkTheme = getDarkThemePreference()
            PFToolTheme(darkTheme, getDynamicThemePreference()) {
                TopToastBase(backgroundColor = MaterialTheme.colorScheme.background, manager = topToastManager, content = { Navigation() })
                SystemBars(darkTheme)
            }
        }
    }

    @Composable
    private fun SystemBars(darkTheme: Boolean) {
        val controller = rememberSystemUiController()
        controller.statusBarDarkContentEnabled = !darkTheme
    }

    @Composable
    private fun Navigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = NavRoutes.MAPS) {
            composable(route = NavRoutes.MAPS) {
                MapsScreenRoot(navController, topToastManager, config, mapsDir)
            }
            composable(route = NavRoutes.OPTIONS) {
                OptionsScreen(navController, topToastManager, config)
            }
        }
    }

    private fun setConfig() {
        if (!config.contains(ConfigKey.KEY_MAPS_DIR)) configEditor.putString(ConfigKey.KEY_MAPS_DIR, defaultMapsDir)
        if (!config.contains(ConfigKey.KEY_MAPS_EXPORT_DIR)) configEditor.putString(ConfigKey.KEY_MAPS_EXPORT_DIR, defaultMapsExportDir)
        configEditor.apply()
    }

    @Composable
    private fun getDarkThemePreference(): Boolean {
        return when(config.getInt(ConfigKey.KEY_APP_THEME, Theme.SYSTEM)) {
            Theme.LIGHT -> false
            Theme.DARK -> true
            else -> isSystemInDarkTheme()
        }
    }

    private fun getDynamicThemePreference(): Boolean {
        return config.getBoolean(ConfigKey.KEY_APP_DYNAMIC_COLORS, true)
    }
}