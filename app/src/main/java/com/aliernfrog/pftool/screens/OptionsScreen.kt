package com.aliernfrog.pftool.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.composables.BaseScaffold

@Composable
fun OptionsScreen(navController: NavController) {
    BaseScaffold(title = LocalContext.current.getString(R.string.options), navController = navController) {
        Text(text = "Options will be available here")
    }
}