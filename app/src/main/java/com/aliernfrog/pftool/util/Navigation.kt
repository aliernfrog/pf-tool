package com.aliernfrog.pftool.util

import androidx.annotation.StringRes
import com.aliernfrog.pftool.R

object NavigationConstant {
    val INITIAL_DESTINATION = Destination.MAPS
}

enum class Destination(
    @StringRes val label: Int
) {
    MAPS(
        label = R.string.maps
    )
}