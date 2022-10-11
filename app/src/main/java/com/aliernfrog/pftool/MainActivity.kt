package com.aliernfrog.pftool

import android.content.SharedPreferences
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        config = getSharedPreferences(ConfigKey.PREF_NAME, MODE_PRIVATE)
        configEditor = config.edit()
        topToastManager = TopToastManager()
        setContent {
            val darkTheme = getDarkThemePreference()
            PFToolTheme(darkTheme, getDynamicColorsPreference()) {
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
                MapsScreenRoot(navController, topToastManager, config)
            }
            composable(route = NavRoutes.OPTIONS) {
                OptionsScreen(navController, topToastManager, config)
            }
        }
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