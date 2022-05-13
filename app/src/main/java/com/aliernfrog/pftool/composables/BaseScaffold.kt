package com.aliernfrog.pftool.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BaseScaffold(title: String, content: @Composable (ColumnScope.() -> Unit)) {
    Scaffold(
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopAppBar(backgroundColor = MaterialTheme.colors.secondary,
                contentPadding = PaddingValues(horizontal = 24.dp),
                content = { Text(text = title, color = MaterialTheme.colors.onSecondary) })
        },
        content = {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Column(Modifier.padding(horizontal = 24.dp), content = content)
            }
        }
    )
}