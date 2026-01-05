package com.aliernfrog.pftool.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.pftool_shared.impl.ShizukuManager

class ShizukuViewModel(
    val prefs: PreferenceManager,
    val topToastState: TopToastState,
    val shizukuManager: ShizukuManager
) : ViewModel()