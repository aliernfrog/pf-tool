package com.aliernfrog.pftool.util.manager

import android.content.Context
import com.aliernfrog.pftool.ConfigKey
import com.aliernfrog.pftool.ui.theme.Theme
import com.aliernfrog.pftool.util.manager.base.BasePreferenceManager

class PreferenceManager(context: Context) : BasePreferenceManager(
    prefs = context.getSharedPreferences(ConfigKey.PREF_NAME, Context.MODE_PRIVATE)
) {
    // Appearance options
    var theme by intPreference(ConfigKey.KEY_APP_THEME, Theme.SYSTEM.ordinal)
    var materialYou by booleanPreference(ConfigKey.KEY_APP_MATERIAL_YOU, true)

    // General options
    var showChosenMapThumbnail by booleanPreference(ConfigKey.KEY_SHOW_CHOSEN_MAP_THUMBNAIL, true)
    var showMapThumbnailsInList by booleanPreference(ConfigKey.KEY_SHOW_MAP_THUMBNAILS_LIST, true)
    var language by stringPreference(ConfigKey.KEY_APP_LANGUAGE, "") // follow system if blank

    // Directory options
    var pfMapsDir by stringPreference(ConfigKey.KEY_MAPS_DIR, ConfigKey.RECOMMENDED_MAPS_DIR)
    var exportedMapsDir by stringPreference(ConfigKey.KEY_EXPORTED_MAPS_DIR, ConfigKey.RECOMMENDED_EXPORTED_MAPS_DIR)

    // Experimental (developer) options
    var permissionsSetupGuideLevel by intPreference("permissionsSetupGuideLevel", 0)
    var showMapNameFieldGuide by booleanPreference("showMapNameFieldGuide", true)
    var autoCheckUpdates by booleanPreference(ConfigKey.KEY_APP_AUTO_UPDATES, true)
    var updatesURL by stringPreference(ConfigKey.KEY_APP_UPDATES_URL, ConfigKey.DEFAULT_UPDATES_URL)
}