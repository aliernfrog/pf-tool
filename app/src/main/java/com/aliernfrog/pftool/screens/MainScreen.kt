package com.aliernfrog.pftool.screens

import android.annotation.SuppressLint
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.composables.BaseScaffold
import com.aliernfrog.pftool.composables.MainButton

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    BaseScaffold(title = LocalContext.current.getString(R.string.app_name), content = {
        val context = LocalContext.current
        MainButton(
            title = context.getString(R.string.manageMaps),
            description = context.getString(R.string.manageMapsDescription),
            painter = painterResource(id = R.drawable.map),
            backgroundColor = MaterialTheme.colors.secondary,
            contentColor = MaterialTheme.colors.onSecondary,
            onClick = {
                //TODO
            })
        MainButton(
            title = context.getString(R.string.options),
            description = context.getString(R.string.optionsDescription),
            painter = painterResource(id = R.drawable.options),
            backgroundColor = MaterialTheme.colors.secondary,
            contentColor = MaterialTheme.colors.onSecondary,
            onClick = {
                //TODO
            })
    })
}