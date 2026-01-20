package io.github.aliernfrog.shared.ui.component.util

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun AppContainer(
    content: @Composable BoxScope.() -> Unit
) {
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val navbarInsets = WindowInsets.navigationBars
    val navbarOnLeft = navbarInsets.getLeft(density, layoutDirection) > 0
    val navbarOnRight = navbarInsets.getRight(density, layoutDirection) > 0

    Box(
        content = content,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .let {
                var modifier = it
                if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) modifier = modifier.displayCutoutPadding()
                if (navbarOnLeft || navbarOnRight) modifier = modifier.navigationBarsPadding()
                modifier
            }
    )
}