package com.aliernfrog.pftool

import com.aliernfrog.pftool.data.Social

object ConfigKey {
    const val PREF_NAME = "APP_CONFIG"
    const val KEY_APP_THEME = "appTheme"
    const val KEY_MAPS_DIR = "mapsDir"
    const val KEY_MAPS_EXPORT_DIR = "mapsExportDir"
    const val DEFAULT_MAPS_DIR = "%STORAGE%/Android/data/com.MA.Polyfield/files/editor"
    const val DEFAULT_MAPS_EXPORT_DIR = "%DOCUMENTS%/PFTool/exported"
}

object Link {
    val socials = listOf(
        Social("Polyfield Discord", "https://discord.gg/X6WzGpCgDJ"),
        Social("PF Tool GitHub", "https://github.com/aliernfrog/pf-tool")
    )
}

object NavRoutes {
    const val MAIN = "main"
    const val MAPS = "maps"
    const val OPTIONS = "options"
}

object Theme {
    const val SYSTEM = 0
    const val LIGHT = 1
    const val DARK = 2
}