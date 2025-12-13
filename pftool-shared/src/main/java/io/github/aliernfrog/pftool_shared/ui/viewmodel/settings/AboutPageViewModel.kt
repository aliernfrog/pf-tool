package io.github.aliernfrog.pftool_shared.ui.viewmodel.settings

import androidx.lifecycle.ViewModel
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.pftool_shared.impl.VersionManager

class AboutPageViewModel(
    val topToastState: TopToastState,
    versionManager: VersionManager
) : ViewModel() {
    val applicationVersionLabel = versionManager.versionLabel
    val updateAvailable = versionManager.updateAvailable
}