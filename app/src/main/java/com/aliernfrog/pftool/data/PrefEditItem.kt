package com.aliernfrog.pftool.data

import com.aliernfrog.pftool.util.manager.PreferenceManager

data class PrefEditItem(
    val labelResourceId: Int,
    val getValue: (prefs: PreferenceManager) -> String,
    val setValue: (newValue: String, prefs: PreferenceManager) -> Unit,
    val default: String = ""
)
