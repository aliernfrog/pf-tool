package io.github.aliernfrog.shared.ui.component.util

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomSpacer(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .navigationBarsPadding()
            .heightIn(min = 12.dp)
    )
}