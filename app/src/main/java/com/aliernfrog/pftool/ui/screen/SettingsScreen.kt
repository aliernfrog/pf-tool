package com.aliernfrog.pftool.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.pftool.ui.viewmodel.SettingsViewModel
import io.github.aliernfrog.shared.di.getKoinInstance
import io.github.aliernfrog.shared.ui.settings.SettingsDestination
import io.github.aliernfrog.shared.ui.settings.SettingsRootPage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: SettingsViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit,
    onNavigateRequest: (SettingsDestination) -> Unit
) {
    val scope = rememberCoroutineScope()
    val updateAvailable = vm.versionManager.updateAvailable.collectAsState().value
    val latestVersionInfo = vm.versionManager.latestVersionInfo.collectAsState().value

    SettingsRootPage(
        categories = vm.categories,
        updateAvailable = updateAvailable,
        latestReleaseInfo = latestVersionInfo,
        onShowUpdateSheetRequest = { scope.launch {
            // TODO remove MainViewModel dependency
            getKoinInstance<MainViewModel>().updateSheetState.show()
        } },
        onNavigateBackRequest = onNavigateBackRequest,
        onNavigateRequest = onNavigateRequest
    )
}