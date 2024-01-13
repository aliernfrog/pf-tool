package com.aliernfrog.pftool.data

import com.aliernfrog.pftool.R

data class MapActionResult(
    val successful: Boolean,
    val messageId: Int? = if (successful) null else R.string.warning_error,
    val newFile: Any? = null
)
