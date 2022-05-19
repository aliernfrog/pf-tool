package com.aliernfrog.pftool.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.composable.PFToolButton

@Composable
fun MainScreen(navController: NavController) {
    PFToolBaseScaffold(title = LocalContext.current.getString(R.string.app_name), navController = navController) {
        val context = LocalContext.current
        PFToolButton(
            title = context.getString(R.string.manageMaps),
            description = context.getString(R.string.manageMapsDescription),
            painter = painterResource(id = R.drawable.map),
            onClick = {
                navController.navigate("maps")
            })
        PFToolButton(
            title = context.getString(R.string.options),
            description = context.getString(R.string.optionsDescription),
            painter = painterResource(id = R.drawable.options),
            onClick = {
                navController.navigate("options")
            })
    }
}