package io.github.aliernfrog.shared.util.extension

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PriorityHigh
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState

fun TopToastState.showErrorToast(text: Any) {
    showToast(
        text = text,
        icon = Icons.Rounded.PriorityHigh,
        iconTintColor = TopToastColor.ERROR
    )
}