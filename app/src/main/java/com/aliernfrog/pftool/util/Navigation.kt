package com.aliernfrog.pftool.util

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.PinDrop
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.ui.graphics.Color
import androidx.navigation3.ui.NavDisplay
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

object UpdateScreenDestination

val slideTransitionMetadata = NavDisplay.transitionSpec {
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Start
    ) + fadeIn() togetherWith slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Start
    ) + fadeOut()
} + NavDisplay.popTransitionSpec {
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.End
    ) togetherWith slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.End
    )
} + NavDisplay.predictivePopTransitionSpec {
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.End
    ) togetherWith slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.End
    )
}

val slideVerticalTransitionMetadata = NavDisplay.transitionSpec {
    slideInVertically(
        initialOffsetY = { it }
    ) + fadeIn() togetherWith slideOutVertically(
        targetOffsetY = { -it }
    ) + fadeOut()
} + NavDisplay.popTransitionSpec {
    slideInVertically(
        initialOffsetY = { -it }
    ) + fadeIn() togetherWith slideOutVertically(
        targetOffsetY = { -it }
    ) + fadeOut()
} + NavDisplay.predictivePopTransitionSpec {
    slideInVertically(
        initialOffsetY = { -it }
    ) + fadeIn() togetherWith slideOutVertically(
        targetOffsetY = { it }
    ) + fadeOut()
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