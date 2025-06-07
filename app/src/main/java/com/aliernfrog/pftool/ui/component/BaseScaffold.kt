package com.aliernfrog.pftool.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BaseScaffold(
    content: @Composable (
        paddingValues: PaddingValues
    ) -> Unit
) {
    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        content(it)
    }
}