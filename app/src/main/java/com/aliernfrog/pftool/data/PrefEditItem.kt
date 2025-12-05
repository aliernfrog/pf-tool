package com.aliernfrog.pftool.data

import com.aliernfrog.pftool.util.manager.PreferenceManager
import io.github.aliernfrog.pftool_shared.util.manager.base.BasePreferenceManager

data class PrefEditItem<T>(
    val preference: (PreferenceManager) -> BasePreferenceManager.Preference<T>,
    val label: (PreferenceManager) -> Any = { preference(it).key }
)