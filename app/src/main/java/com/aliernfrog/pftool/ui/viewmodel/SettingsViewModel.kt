package com.aliernfrog.pftool.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.experimentalSettingsRequiredClicks
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState

class SettingsViewModel(
    val prefs: PreferenceManager,
    val topToastState: TopToastState
) : ViewModel() {
    var experimentalSettingsShown by mutableStateOf(false)
    private var aboutClickCount by mutableIntStateOf(0)

    fun onAboutClick() {
        if (aboutClickCount > experimentalSettingsRequiredClicks) return
        aboutClickCount++
        if (aboutClickCount == experimentalSettingsRequiredClicks) {
            experimentalSettingsShown = true
            topToastState.showToast(
                text = R.string.settings_experimental_enabled,
                icon = Icons.Rounded.Build,
                iconTintColor = TopToastColor.ON_SURFACE
            )
        }
    }
}