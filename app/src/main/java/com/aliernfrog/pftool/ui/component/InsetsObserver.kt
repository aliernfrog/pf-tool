package com.aliernfrog.pftool.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.aliernfrog.pftool.ui.viewmodel.InsetsViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun InsetsObserver(
    insetsViewModel: InsetsViewModel = getViewModel()
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
                insetsViewModel.topPadding = toDp(it.height)
            }
            .statusBarsPadding()

    )

    // Navigation bar
    Spacer(
        modifier = Modifier
            .onSizeChanged {
                insetsViewModel.bottomPadding = toDp(it.height)
            }
            .navigationBarsPadding()

    )
}