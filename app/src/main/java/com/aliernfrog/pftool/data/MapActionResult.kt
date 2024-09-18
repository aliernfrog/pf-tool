package com.aliernfrog.pftool.data

import androidx.annotation.StringRes
import com.aliernfrog.pftool.R

data class MapActionResult(
    val successful: Boolean,
    @StringRes val message: Int? = if (successful) null else R.string.warning_error,
    val newFile: Any? = null
)
