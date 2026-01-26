package io.github.aliernfrog.shared.ui.viewmodel.settings

import androidx.lifecycle.ViewModel
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.shared.impl.VersionManager

class AboutPageViewModel(
    val topToastState: TopToastState,
    versionManager: VersionManager
) : ViewModel() {
    val applicationVersionLabel = versionManager.versionLabel
    val availableUpdates = versionManager.availableUpdates
}