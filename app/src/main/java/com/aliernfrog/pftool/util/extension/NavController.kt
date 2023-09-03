package com.aliernfrog.pftool.util.extension

import androidx.navigation.NavController
import com.aliernfrog.pftool.util.Destination

/**
 * Navigates to given [destination] and removes previous destinations from back stack.
 */
fun NavController.set(destination: Destination) {
    navigate(destination.route) { popUpTo(0) }
}

/**
 * Navigates to given [destination].
 */
fun NavController.navigate(destination: Destination) {
    navigate(destination.route)
}

/**
 * If a previous back stack entry exists, pops back stack. Navigates to [fallback] if not.
 */
fun NavController.popBackStackSafe(
    fallback: Destination? = null
) {
    if (previousBackStackEntry != null) popBackStack()
    else fallback?.let { set(it) }
}