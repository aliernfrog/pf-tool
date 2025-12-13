package com.aliernfrog.pftool.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import com.aliernfrog.pftool.ui.screen.SettingsScreen
import io.github.aliernfrog.pftool_shared.ui.screen.settings.LibsPage
import io.github.aliernfrog.pftool_shared.ui.screen.settings.SettingsDestination

object NavigationConstant {
    val INITIAL_DESTINATION = Destination.MAPS
}

val settingsRootDestination = SettingsDestination(
    title = "",
    description = "",
    icon = Icons.Rounded.Settings,
    content = { onNavigateBackRequest, onNavigateRequest ->
        SettingsScreen(
            onNavigateRequest = onNavigateRequest,
            onNavigateBackRequest = onNavigateBackRequest
        )
    }
)

val settingsLibsDestination = SettingsDestination(
    title = "",
    description = "",
    icon = Icons.Rounded.Settings,
    content = { onNavigateBackRequest, _ ->
        LibsPage(onNavigateBackRequest = onNavigateBackRequest)
    }
)

enum class Destination {
    MAPS
}