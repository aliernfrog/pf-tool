package com.aliernfrog.pftool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.pftool.ui.screen.MainScreen
import com.aliernfrog.pftool.ui.screen.MapsScreen
import com.aliernfrog.pftool.ui.screen.OptionsScreen
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    private fun getDarkThemePreference(): Boolean? {
        val prefs = getSharedPreferences("APP_CONFIG", MODE_PRIVATE)
        val theme = prefs.getInt("appTheme", 0) //system
        if (theme == 1) return false //light
        if (theme == 2) return true //dark
        return null
    }

    @Composable
    fun SystemBars() {
        val systemUiController = rememberSystemUiController()
        systemUiController.setSystemBarsColor(MaterialTheme.colors.background)
    }

    @Composable
    fun Navigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "main") {
            composable(route = "main") {
                MainScreen(navController)
            }
            composable(route = "maps") {
                MapsScreen(navController)
            }
            composable(route = "options") {
                OptionsScreen(navController)
            }
        }
    }
}