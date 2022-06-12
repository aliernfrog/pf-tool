package com.aliernfrog.pftool

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.pftool.ui.screen.MainScreen
import com.aliernfrog.pftool.ui.screen.MapsScreen
import com.aliernfrog.pftool.ui.screen.OptionsScreen
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    private lateinit var config: SharedPreferences
    private lateinit var configEditor: SharedPreferences.Editor

    private val defaultMapsDir = "${Environment.getExternalStorageDirectory()}/Android/data/com.MA.Polyfield/files/editor"
    private val defaultMapsExportDir = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/PFTool/exported"
    private val defaultUriSdkVersion = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        config = getSharedPreferences("APP_CONFIG", MODE_PRIVATE)
        configEditor = config.edit()
        setConfig()
        setContent {
            PFToolTheme(getDarkThemePreference() ?: isSystemInDarkTheme()) {
                Box(modifier = Modifier
                    .background(MaterialTheme.colors.background)
                    .fillMaxSize())
                SystemBars()
                Navigation()
            }
        }
    }

    @Composable
    private fun SystemBars() {
        val systemUiController = rememberSystemUiController()
        systemUiController.setSystemBarsColor(MaterialTheme.colors.background)
    }

    @Composable
    private fun Navigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "main") {
            composable(route = "main") {
                MainScreen(navController, config)
            }
            composable(route = "maps") {
                MapsScreen(navController, config, getMapsTreeDocumentFile())
            }
            composable(route = "options") {
                OptionsScreen(navController, config)
            }
        }
    }

    private fun getMapsTreeDocumentFile(): DocumentFile? {
        val uriSdkVersion = config.getInt("uriSdkVersion", defaultUriSdkVersion)
        if (Build.VERSION.SDK_INT >= uriSdkVersion) {
            val treeId = config.getString("mapsDir", defaultMapsDir)?.replace("${Environment.getExternalStorageDirectory()}/", "primary:")
            val treeUri = DocumentsContract.buildTreeDocumentUri("com.android.externalstorage.documents", treeId)
            return DocumentFile.fromTreeUri(applicationContext, treeUri)
        }
        return null
    }

    private fun setConfig() {
        if (!config.contains("mapsDir")) configEditor.putString("mapsDir", defaultMapsDir)
        if (!config.contains("mapsExportDir")) configEditor.putString("mapsExportDir", defaultMapsExportDir)
        if (!config.contains("uriSdkVersion")) configEditor.putInt("uriSdkVersion", defaultUriSdkVersion)
        configEditor.apply()
    }

    private fun getDarkThemePreference(): Boolean? {
        val theme = config.getInt("appTheme", 0) //system
        if (theme == 1) return false //light
        if (theme == 2) return true //dark
        return null
    }
}