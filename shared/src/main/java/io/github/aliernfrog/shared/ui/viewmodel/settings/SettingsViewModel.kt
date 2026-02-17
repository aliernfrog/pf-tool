package io.github.aliernfrog.shared.ui.viewmodel.settings

import androidx.lifecycle.ViewModel
import io.github.aliernfrog.shared.impl.VersionManager

class SettingsViewModel(
    versionManager: VersionManager
) : ViewModel() {
    val versionLabel = versionManager.versionLabel
}