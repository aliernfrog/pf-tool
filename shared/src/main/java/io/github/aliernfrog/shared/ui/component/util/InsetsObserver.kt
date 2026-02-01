package io.github.aliernfrog.shared.ui.component.util

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import io.github.aliernfrog.shared.impl.InsetsManager
import org.koin.compose.koinInject

@Composable
fun InsetsObserver(
    insetsManager: InsetsManager = koinInject()
) {
    val density = LocalDensity.current
    fun toDp(pxs: Int): Dp {
        with(density) {
            return pxs.toDp()
        }
    }

    // Status bar
    Spacer(
        modifier = Modifier
            .onSizeChanged {
                insetsManager.topPadding = toDp(it.height)
            }
            .statusBarsPadding()
    )

    // Navigation bar
    Spacer(
        modifier = Modifier
            .onSizeChanged {
                insetsManager.bottomPadding = toDp(it.height)
            }
            .navigationBarsPadding()
    )

    // IME
    Spacer(
        modifier = Modifier
            .onSizeChanged {
                insetsManager.imePadding = toDp(it.height)
            }
            .imePadding()
    )
}