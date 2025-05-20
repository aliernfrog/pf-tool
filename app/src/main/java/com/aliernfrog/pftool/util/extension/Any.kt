package com.aliernfrog.pftool.util.extension

import com.aliernfrog.pftool.di.getKoinInstance
import com.aliernfrog.pftool.util.manager.ContextUtils

fun Any.resolveString(throwOnUnknownClass: Boolean = false): String {
    val contextUtils = getKoinInstance<ContextUtils>()
    return when (this) {
        is String -> this
        is Int -> contextUtils.getString(this)
        else -> if (throwOnUnknownClass) throw IllegalArgumentException("resolveString: unexpected class") else "UNKNOWN CLASS: ${this.javaClass.name}"
    }
}