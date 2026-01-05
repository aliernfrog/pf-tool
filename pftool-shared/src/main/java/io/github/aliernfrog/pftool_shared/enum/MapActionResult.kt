package io.github.aliernfrog.pftool_shared.enum

import androidx.annotation.StringRes

data class MapActionResult(
    val successful: Boolean,
    @StringRes val message: Int? = null,
    val newFile: Any? = null
)