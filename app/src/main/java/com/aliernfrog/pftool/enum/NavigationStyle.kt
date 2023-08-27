package com.aliernfrog.pftool.enum

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

enum class NavigationStyle {
    NAVIGATION_BAR {
        override fun shouldBeUsed(windowSizeClass: WindowSizeClass): Boolean {
            return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
        }
    },

    NAVIGATION_RAIL {
        override fun shouldBeUsed(windowSizeClass: WindowSizeClass): Boolean {
            return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium
        }
    },

    NAVIGATION_DRAWER {
        override fun shouldBeUsed(windowSizeClass: WindowSizeClass): Boolean {
            return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
        }
    };

    abstract fun shouldBeUsed(windowSizeClass: WindowSizeClass): Boolean
}

fun getNavigationStyle(windowSizeClass: WindowSizeClass): NavigationStyle {
    return NavigationStyle.values().find {
        it.shouldBeUsed(windowSizeClass)
    } ?: NavigationStyle.NAVIGATION_BAR
}