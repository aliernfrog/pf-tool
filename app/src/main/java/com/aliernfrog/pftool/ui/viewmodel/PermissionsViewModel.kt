package com.aliernfrog.pftool.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.enum.PermissionSetupGuideLevel
import com.aliernfrog.pftool.util.manager.PreferenceManager

class PermissionsViewModel(
    private val prefs: PreferenceManager
) : ViewModel() {
    var guideLevel: PermissionSetupGuideLevel
        get() = PermissionSetupGuideLevel.entries[prefs.permissionsSetupGuideLevel]
        set(value) { prefs.permissionsSetupGuideLevel = value.ordinal }

    fun pushGuideLevel() {
        val newIndex = guideLevel.ordinal+1
        PermissionSetupGuideLevel.entries.let {
            if (newIndex > it.size-1) return
            guideLevel = PermissionSetupGuideLevel.entries[newIndex]
        }
    }
}