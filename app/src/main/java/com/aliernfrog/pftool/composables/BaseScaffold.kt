package com.aliernfrog.pftool.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BaseScaffold(title: String, showBackButton: Boolean, content: @Composable (ColumnScope.() -> Unit)) {
    Scaffold(
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopAppBar(backgroundColor = MaterialTheme.colors.secondary,
                contentPadding = PaddingValues(horizontal = 24.dp),
                content = {
                    if (showBackButton) {
                        Image(
                            rememberVectorPainter(image = Icons.Filled.ArrowBack),
                            contentDescription = LocalContext.current.getString(R.string.action_back),
                            Modifier.padding(end = 24.dp)
                        )
                    }
                    Text(text = title, color = MaterialTheme.colors.onSecondary)
                })
        },
        content = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())) {
                Column(Modifier.padding(horizontal = 24.dp), content = content)
            }
        }
    )
}