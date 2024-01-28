package com.aliernfrog.pftool.util.extension

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PriorityHigh
import com.aliernfrog.pftool.R
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState

fun TopToastState.showMapAlreadyExistsToast() {
    showErrorToast(R.string.maps_alreadyExists)
}

fun TopToastState.showErrorToast(text: Any = R.string.warning_error) {
    showToast(
        text = text,
        icon = Icons.Rounded.PriorityHigh,
        iconTintColor = TopToastColor.ERROR
    )
}