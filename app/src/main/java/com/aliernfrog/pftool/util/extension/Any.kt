package com.aliernfrog.pftool.util.extension

import com.aliernfrog.pftool.util.getKoinInstance
import com.aliernfrog.pftool.util.manager.ContextUtils

fun Any.resolveString(): String {
    val contextUtils = getKoinInstance<ContextUtils>()
    return when (this) {
        is String -> this
        is Int -> contextUtils.getString(this)
        else -> throw IllegalArgumentException("resolveString: unexpected class")
    }
}