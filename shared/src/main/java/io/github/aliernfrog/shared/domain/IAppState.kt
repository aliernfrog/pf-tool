package io.github.aliernfrog.shared.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class IAppState {
    var lastCaughtException by mutableStateOf<Throwable?>(null)
}