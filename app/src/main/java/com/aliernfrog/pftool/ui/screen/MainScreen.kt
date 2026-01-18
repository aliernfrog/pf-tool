package com.aliernfrog.pftool.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.ui.screen.maps.MapsScreen
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.pftool.util.Destination
import com.aliernfrog.pftool.util.UpdateScreenDestination
import com.aliernfrog.pftool.util.extension.removeLastIfMultiple
import com.aliernfrog.pftool.util.slideTransitionMetadata
import com.aliernfrog.pftool.util.slideVerticalTransitionMetadata
import io.github.aliernfrog.pftool_shared.ui.dialog.ProgressDialog
import io.github.aliernfrog.shared.ui.screen.UpdatesScreen
import io.github.aliernfrog.shared.ui.settings.SettingsDestination
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    vm: MainViewModel = koinViewModel()
) {
    val availableUpdates = vm.availableUpdates.collectAsStateWithLifecycle().value
    val currentVersionInfo = vm.currentVersionInfo.collectAsStateWithLifecycle().value
    val isCompatibleWithLatestVersion = vm.isCompatibleWithLatestVersion.collectAsStateWithLifecycle().value
    val isCheckingForUpdates = vm.isCheckingForUpdates.collectAsStateWithLifecycle().value

    val onNavigateBackRequest: () -> Unit = {
        vm.navigationBackStack.removeLastIfMultiple()
    }

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        NavDisplay(
            backStack = vm.navigationBackStack,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding(),
            entryProvider = entryProvider {
                entry<Destination> { destination ->
                    when (destination) {
                        Destination.MAPS -> {
                            MapsScreen(
                                map = null,
                                onNavigateRequest = { vm.navigationBackStack.add(it) },
                                onNavigateBackRequest = null
                            )
                        }
                    }
                }

                entry<SettingsDestination>(
                    metadata = slideTransitionMetadata
                ) { destination ->
                    SettingsScreen(
                        destination = destination,
                        onNavigateBackRequest = onNavigateBackRequest,
                        onNavigateRequest = { vm.navigationBackStack.add(it) },
                        onCheckUpdatesRequest = { skipVersionCheck ->
                            vm.checkUpdates(skipVersionCheck = skipVersionCheck)
                        },
                        onNavigateUpdatesScreenRequest = {
                            vm.navigationBackStack.add(UpdateScreenDestination)
                        }
                    )
                }

                entry<MapFile>(
                    metadata = slideTransitionMetadata
                ) { map ->
                    MapsScreen(
                        map = map,
                        onNavigateRequest = { vm.navigationBackStack.add(it) },
                        onNavigateBackRequest = onNavigateBackRequest
                    )
                }

                entry<UpdateScreenDestination>(
                    metadata = slideVerticalTransitionMetadata
                ) {
                    UpdatesScreen(
                        availableUpdates = availableUpdates,
                        currentVersionInfo = currentVersionInfo,
                        isCheckingForUpdates = isCheckingForUpdates,
                        isCompatibleWithLatestVersion = isCompatibleWithLatestVersion,
                        onCheckUpdatesRequest = {
                            vm.checkUpdates(manuallyTriggered = true)
                        },
                        onNavigateBackRequest = onNavigateBackRequest
                    )
                }
            }
        )
    }

    vm.progressState.currentProgress?.let {
        ProgressDialog(it) {
            vm.progressState.currentProgress = null
        }
    }
}