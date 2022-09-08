package com.aliernfrog.pftool

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.pftool.ui.screen.MainScreen
import com.aliernfrog.pftool.ui.screen.MapsExportedScreen
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

    private val defaultMapsDir = "${Environment.getExternalStorageDirectory()}/Android/data/com.MA.Polyfield/files/editor"
    private val defaultMapsExportDir = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/PFTool/exported"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        config = getSharedPreferences("APP_CONFIG", MODE_PRIVATE)
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
        NavHost(navController = navController, startDestination = "main", modifier = Modifier.imePadding()) {
            composable(route = "main") {
                MainScreen(navController, config)
            }
            composable(route = "maps") {
                MapsScreen(navController, topToastManager, config, getMapsFile())
            }
            composable(route = "mapsExported") {
                MapsExportedScreen(navController, topToastManager, config)
            }
            composable(route = "options") {
                OptionsScreen(navController, topToastManager, config)
            }
        }
    }

    private fun getMapsFile(): DocumentFileCompat {
        val treeId = config.getString("mapsDir", defaultMapsDir)?.replace("${Environment.getExternalStorageDirectory()}/", "primary:")
        val treeUri = DocumentsContract.buildTreeDocumentUri("com.android.externalstorage.documents", treeId)
        return DocumentFileCompat.fromTreeUri(applicationContext, treeUri)!!
    }

    private fun setConfig() {
        if (!config.contains("mapsDir")) configEditor.putString("mapsDir", defaultMapsDir)
        if (!config.contains("mapsExportDir")) configEditor.putString("mapsExportDir", defaultMapsExportDir)
        configEditor.apply()
    }

    private fun getDarkThemePreference(): Boolean? {
        val theme = config.getInt("appTheme", 0) //system
        if (theme == 1) return false //light
        if (theme == 2) return true //dark
        return null
    }
}