package io.github.aliernfrog.shared.ui.component.util

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedVisibilityShadowWorkaround(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier.offset(x = 16.dp, y = 16.dp)
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun <S> AnimatedContentShadowWorkaround(
    targetState: S,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(S) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier.offset(x = 16.dp, y = 16.dp)
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            content(it)
        }
    }
}