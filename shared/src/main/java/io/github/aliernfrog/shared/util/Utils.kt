package io.github.aliernfrog.shared.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Update
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.shared.di.getKoinInstance
import io.github.aliernfrog.shared.impl.ContextUtils

const val TAG = "AliernfrogSharedLogs"

fun sdkVersionToAndroidVersion(sdkVersion: Int): String = when (sdkVersion) {
    21 -> "5.0"
    22 -> "5.1"
    23 -> "6.0"
    24 -> "7.0"
    25 -> "7.1"
    26 -> "8.0"
    27 -> "8.1"
    28 -> "9.0"
    29 -> "10"
    30 -> "11"
    31 -> "12"
    32 -> "12L (12.1)"
    33 -> "13"
    34 -> "14"
    35 -> "15"
    36 -> "16"
    37 -> "17"
    38 -> "18"
    39 -> "19"
    40 -> "20"
    else -> "SDK $sdkVersion"
}

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