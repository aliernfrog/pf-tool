package com.aliernfrog.pftool

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.pftool.ui.screen.MainScreen
import com.aliernfrog.pftool.ui.screen.MapsScreen
import com.aliernfrog.pftool.ui.screen.OptionsScreen
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.aliernfrog.toptoast.TopToastBase
import com.aliernfrog.toptoast.TopToastManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lazygeniouz.filecompat.file.DocumentFileCompat

class MainActivity : ComponentActivity() {
    private lateinit var config: SharedPreferences
    private lateinit var configEditor: SharedPreferences.Editor
    private lateinit var topToastManager: TopToastManager

    private val defaultMapsDir = ConfigKey.DEFAULT_MAPS_DIR.replace("%STORAGE%", Environment.getExternalStorageDirectory().toString())
    private val defaultMapsExportDir = ConfigKey.DEFAULT_MAPS_EXPORT_DIR.replace("%DOCUMENTS%", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        config = getSharedPreferences(ConfigKey.PREF_NAME, MODE_PRIVATE)
        configEditor = config.edit()
        topToastManager = TopToastManager()
        setConfig()
        setContent {
            val darkTheme = getDarkThemePreference() ?: isSystemInDarkTheme()
            PFToolTheme(darkTheme) {
                TopToastBase(backgroundColor = MaterialTheme.colors.background, manager = topToastManager, content = { Navigation() })
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
        NavHost(navController = navController, startDestination = NavRoutes.MAIN) {
            composable(route = NavRoutes.MAIN) {
                MainScreen(navController, config)
            }
            composable(route = NavRoutes.MAPS) {
                MapsScreen(navController, topToastManager, config, getMapsFile())
            }
            composable(route = NavRoutes.OPTIONS) {
                OptionsScreen(navController, topToastManager, config)
            }
        }
    }

    private fun getMapsFile(): DocumentFileCompat {
        val treeId = config.getString(ConfigKey.KEY_MAPS_DIR, defaultMapsDir)?.replace("${Environment.getExternalStorageDirectory()}/", "primary:")
        val treeUri = DocumentsContract.buildTreeDocumentUri("com.android.externalstorage.documents", treeId)
        return DocumentFileCompat.fromTreeUri(applicationContext, treeUri)!!
    }

    private fun setConfig() {
        if (!config.contains(ConfigKey.KEY_MAPS_DIR)) configEditor.putString(ConfigKey.KEY_MAPS_DIR, defaultMapsDir)
        if (!config.contains(ConfigKey.KEY_MAPS_EXPORT_DIR)) configEditor.putString(ConfigKey.KEY_MAPS_EXPORT_DIR, defaultMapsExportDir)
        configEditor.apply()
    }

    private fun getDarkThemePreference(): Boolean? {
        return when(config.getInt(ConfigKey.KEY_APP_THEME, Theme.SYSTEM)) {
            Theme.LIGHT -> false
            Theme.DARK -> true
            else -> null
        }
    }
}