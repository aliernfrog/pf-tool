package com.aliernfrog.pftool.ui.screen

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.ui.screen.maps.MapsScreen
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.pftool.util.Destination
import com.aliernfrog.pftool.util.extension.removeLastIfMultiple
import io.github.aliernfrog.pftool_shared.ui.dialog.ProgressDialog
import io.github.aliernfrog.shared.ui.settings.SettingsDestination
import io.github.aliernfrog.shared.ui.sheet.UpdateSheet
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Suppress("MoveLambdaOutsideParentheses")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    vm: MainViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()

    val updateAvailable = vm.updateAvailable.collectAsState()
    val latestVersionInfo = vm.latestVersionInfo.collectAsState()

    val onNavigateBackRequest: () -> Unit = {
        vm.navigationBackStack.removeLastIfMultiple()
    }

    val slideTransitionMetadata = NavDisplay.transitionSpec {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Start
        ) + fadeIn() togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Start
        ) + fadeOut()
    } + NavDisplay.popTransitionSpec {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.End
        ) togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.End
        )
    } + NavDisplay.predictivePopTransitionSpec {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.End
        ) togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.End
        )
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
                        onShowUpdateSheetRequest = { scope.launch {
                            vm.updateSheetState.show()
                        } }
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
            }
        )
    }

    UpdateSheet(
        sheetState = vm.updateSheetState,
        latestVersionInfo = latestVersionInfo.value,
        updateAvailable = updateAvailable.value,
        onCheckUpdatesRequest = { scope.launch {
            vm.checkUpdates(manuallyTriggered = true)
        } }
    )

    vm.progressState.currentProgress?.let {
        ProgressDialog(it) {
            vm.progressState.currentProgress = null
        }
    }
}