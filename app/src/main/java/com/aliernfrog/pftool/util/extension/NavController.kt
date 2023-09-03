package com.aliernfrog.pftool.util.extension

import androidx.navigation.NavController
import com.aliernfrog.pftool.util.Destination
import com.aliernfrog.pftool.util.currentDestination

fun NavController.set(destination: Destination) {
    navigate(destination.route) { popUpTo(0) }
    destination.subScreen?.let {
        setSubScreen(it)
    }
}

fun NavController.navigate(
    destination: Destination
) {
    navigate(destination.currentDestination.route)
}

fun NavController.setSubScreen(subScreen: Destination) {
    val currentDestination = getCurrentDestination()!!
    currentDestination.subScreen = subScreen
    subScreen.root.value = currentDestination
    navigate(subScreen)
}

fun NavController.getCurrentDestination(): Destination? {
    val currentRoute = currentBackStackEntry?.destination?.route
    return Destination.values().find { it.route == currentRoute }
}