package io.github.aliernfrog.shared.util.extension

import io.github.aliernfrog.shared.di.getKoinInstance
import io.github.aliernfrog.shared.impl.ContextUtils


fun Any.resolveString(throwOnUnknownClass: Boolean = false): String {
    val contextUtils = getKoinInstance<ContextUtils>()
    return when (this) {
        is String -> this
        is Int -> contextUtils.getString(this)
        else -> if (throwOnUnknownClass) throw IllegalArgumentException("resolveString: unexpected class") else "UNKNOWN CLASS: ${this.javaClass.name}"
    }
}