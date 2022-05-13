package com.aliernfrog.pftool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.pftool.screens.MainScreen
import com.aliernfrog.pftool.screens.OptionsScreen
import com.aliernfrog.pftool.ui.theme.PFToolTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PFToolTheme {
                Navigation()
            }
        }
    }

    @Composable
    fun Navigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "main") {
            composable(route = "main") {
                MainScreen(navController)
            }
            composable(route = "options") {
                OptionsScreen()
            }
        }
    }
}