package com.aliernfrog.pftool.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composables.BaseScaffold
import com.aliernfrog.pftool.ui.composables.MainButton

@Composable
fun MainScreen(navController: NavController) {
    BaseScaffold(title = LocalContext.current.getString(R.string.app_name), navController = navController) {
        val context = LocalContext.current
        MainButton(
            title = context.getString(R.string.manageMaps),
            description = context.getString(R.string.manageMapsDescription),
            painter = painterResource(id = R.drawable.map),
            onClick = {
                navController.navigate("maps")
            })
        MainButton(
            title = context.getString(R.string.options),
            description = context.getString(R.string.optionsDescription),
            painter = painterResource(id = R.drawable.options),
            onClick = {
                navController.navigate("options")
            })
    }
}