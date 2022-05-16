package com.aliernfrog.pftool.screens

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.composables.BaseScaffold
import com.aliernfrog.pftool.composables.MainButton

@Composable
fun MapsScreen(navController: NavController) {
    val context = LocalContext.current
    BaseScaffold(title = context.getString(R.string.manageMaps), navController = navController) {
        MainButton(
            title = context.getString(R.string.manageMapsPickMap),
            painter = painterResource(id = R.drawable.map),
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            onClick = {
                //TODO
            })
    }
}