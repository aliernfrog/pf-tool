package com.aliernfrog.pftool.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.aliernfrog.pftool.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    PFToolTheme {
        Scaffold(
            scaffoldState = rememberScaffoldState(),
            topBar = {
                TopAppBar(backgroundColor = MaterialTheme.colors.primary,
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    contentColor = MaterialTheme.colors.onPrimary,
                    content = { Text(text = LocalContext.current.getString(R.string.app_name)) })
            },
            content = {
                Text("test")
            }
        )
    }
}