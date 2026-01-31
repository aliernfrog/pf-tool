package com.aliernfrog.pftool.impl

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aliernfrog.pftool.util.NavigationConstant
import io.github.aliernfrog.pftool_shared.impl.SAFFileCreator
import io.github.aliernfrog.shared.data.MediaOverlayData

class AppState {
    lateinit var safZipFileCreator: SAFFileCreator

    val navigationBackStack = mutableStateListOf<Any>(
        NavigationConstant.INITIAL_DESTINATION
    )

    var showUpdateNotification by mutableStateOf(false)

    var mediaOverlayData by mutableStateOf<MediaOverlayData?>(null)
}