package com.aliernfrog.pftool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.pftool.screens.MainScreen
import com.aliernfrog.pftool.screens.OptionsScreen
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PFToolTheme {
                Box(modifier = Modifier.background(MaterialTheme.colors.background).fillMaxSize())
                SystemBars()
                Navigation()
            }
        }
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
            composable(route = "options") {
                OptionsScreen(navController)
            }
        }
    }
}