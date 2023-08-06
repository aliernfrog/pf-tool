package com.aliernfrog.pftool.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.aliernfrog.pftool.R

object NavigationConstant {
    val INITIAL_DESTINATION = Destination.MAPS.route
}

enum class Destination(
    val route: String,
    val labelId: Int,
    val vectorFilled: ImageVector? = null,
    val vectorOutlined: ImageVector? = null,
    val isSubScreen: Boolean = false,
    var hasNotification: MutableState<Boolean> = mutableStateOf(false)
) {
    MAPS(
        route = "maps",
        labelId = R.string.maps,
        vectorFilled = Icons.Default.PinDrop,
        vectorOutlined = Icons.Outlined.PinDrop
    ),

    SETTINGS(
        route = "settings",
        labelId = R.string.settings,
        vectorFilled = Icons.Default.Settings,
        vectorOutlined = Icons.Outlined.Settings
    )
}