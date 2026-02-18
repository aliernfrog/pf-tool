package io.github.aliernfrog.shared.util.extension

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PriorityHigh
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.shared.di.getKoinInstance
import io.github.aliernfrog.shared.domain.IAppState

fun TopToastState.showErrorToast(
    text: Any,
    duration: Long = 3000,
    onToastClick: (() -> Unit)? = null
) {
    showToast(
        text = text,
        icon = Icons.Rounded.PriorityHigh,
        iconTintColor = TopToastColor.ERROR,
        duration = duration,
        swipeToDismiss = onToastClick != null,
        onToastClick = onToastClick
    )
}

fun TopToastState.showReportableErrorToast(
    text: Any,
    throwable: Throwable,
    duration: Long = 30000
) {
    showToast(
        text = text,
        icon = Icons.Rounded.PriorityHigh,
        iconTintColor = TopToastColor.ERROR,
        duration = duration,
        swipeToDismiss = true,
        onToastClick = {
            getKoinInstance<IAppState>().lastCaughtException = throwable
        }
    )
}