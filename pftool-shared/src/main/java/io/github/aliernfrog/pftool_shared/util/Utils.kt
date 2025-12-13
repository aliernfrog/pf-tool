package io.github.aliernfrog.pftool_shared.util

import android.os.Build
import android.os.Environment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Update
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.pftool_shared.di.getKoinInstance
import io.github.aliernfrog.pftool_shared.impl.ContextUtils

const val TAG = "PFToolSharedLogs"

val externalStorageRoot = Environment.getExternalStorageDirectory().toString()+"/"

val folderPickerSupportsInitialUri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun showUpdateToast(
    onClick: () -> Unit
) {
    val topToastState = getKoinInstance<TopToastState>()
    val contextUtils = getKoinInstance<ContextUtils>()
    topToastState.showToast(
        text = contextUtils.stringFunction {
            it.getSharedString(SharedString.UPDATES_UPDATE_AVAILABLE)
        },
        icon = Icons.Rounded.Update,
        duration = 20000,
        dismissOnClick = true,
        swipeToDismiss = true,
        onToastClick = onClick
    )
}