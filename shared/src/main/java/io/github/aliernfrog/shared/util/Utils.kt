package io.github.aliernfrog.shared.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Update
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.shared.di.getKoinInstance
import io.github.aliernfrog.shared.impl.ContextUtils

const val TAG = "AliernfrogSharedLogs"

fun showUpdateToast(
    onClick: () -> Unit
) {
    val topToastState = getKoinInstance<TopToastState>()
    val contextUtils = getKoinInstance<ContextUtils>()
    topToastState.showToast(
        text = contextUtils.stringFunction {
            it.getSharedString(SharedString.UpdatesUpdateAvailable)
        },
        icon = Icons.Rounded.Update,
        duration = 20000,
        dismissOnClick = true,
        swipeToDismiss = true,
        onToastClick = onClick
    )
}