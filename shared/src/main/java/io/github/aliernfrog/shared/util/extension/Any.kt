package io.github.aliernfrog.shared.util.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun Any.resolveString(throwOnUnknownClass: Boolean = false): String {
    return when (this) {
        is String -> this
        is Int -> stringResource(this)
        else -> if (throwOnUnknownClass) throw IllegalArgumentException("resolveStringComposable: unexpected class")
        else "UNKNOWN CLASS: ${this.javaClass.name}"
    }
}