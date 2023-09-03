package com.aliernfrog.pftool.util.extension

import androidx.navigation.NavController
import com.aliernfrog.pftool.util.Destination

fun NavController.set(destination: Destination) {
    navigate(destination.route) { popUpTo(0) }
}

fun NavController.navigate(destination: Destination) {
    navigate(destination.route)
}

fun NavController.popBackStackSafe(
    fallback: Destination? = null
) {
    if (previousBackStackEntry != null) popBackStack()
    else fallback?.let { set(it) }
}