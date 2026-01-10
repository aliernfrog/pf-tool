package com.aliernfrog.pftool.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.PinDrop
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.ui.graphics.Color
import com.aliernfrog.pftool.R
import io.github.aliernfrog.shared.ui.settings.SettingsDestination
import io.github.aliernfrog.shared.ui.settings.category
import io.github.aliernfrog.shared.util.SharedString

object NavigationConstant {
    val INITIAL_DESTINATION = Destination.MAPS
}

enum class Destination {
    MAPS
}

class AppSettingsDestination {
    companion object {
        val maps = SettingsDestination(
            title = SharedString.fromResId(R.string.settings_maps),
            description = SharedString.fromResId(R.string.settings_maps_description),
            icon = Icons.Rounded.PinDrop,
            iconContainerColor = Color.Green
        )

        val storage = SettingsDestination(
            title = SharedString.fromResId(R.string.settings_storage),
            description = SharedString.fromResId(R.string.settings_storage_description),
            icon = Icons.Rounded.FolderOpen,
            iconContainerColor = Color.Blue
        )

        val language = SettingsDestination(
            title = SharedString.fromResId(R.string.settings_language),
            description = SharedString.fromResId(R.string.settings_language_description),
            icon = Icons.Rounded.Translate,
            iconContainerColor = Color.Magenta
        )
    }
}

val appSettingsCategories = listOf(
    category(
        title = SharedString.fromResId(R.string.settings_category_game)
    ) {
        +AppSettingsDestination.maps
        +AppSettingsDestination.storage
    },

    category(
        title = SharedString.fromResId(R.string.settings_category_app)
    ) {
        +SettingsDestination.appearance
        +AppSettingsDestination.language
        +SettingsDestination.experimental
        +SettingsDestination.about
    }
)