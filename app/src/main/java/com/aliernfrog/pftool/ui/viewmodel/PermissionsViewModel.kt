package com.aliernfrog.pftool.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.util.manager.PreferenceManager

class PermissionsViewModel(
    private val prefs: PreferenceManager
): ViewModel() {
    fun resetShizukuNeverLoadDebugPref() {
        prefs.shizukuNeverLoad.resetValue()
    }
}