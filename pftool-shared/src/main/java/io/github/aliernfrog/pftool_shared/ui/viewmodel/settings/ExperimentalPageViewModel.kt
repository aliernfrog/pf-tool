package io.github.aliernfrog.pftool_shared.ui.viewmodel.settings

import androidx.lifecycle.ViewModel
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.pftool_shared.impl.ProgressState

class ExperimentalPageViewModel(
    val progressState: ProgressState,
    val topToastState: TopToastState
) : ViewModel()