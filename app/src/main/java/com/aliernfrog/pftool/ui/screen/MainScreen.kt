package com.aliernfrog.pftool.ui.screen

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.aliernfrog.pftool.ui.component.BaseScaffold
import com.aliernfrog.pftool.ui.dialog.ProgressDialog
import com.aliernfrog.pftool.ui.screen.maps.MapsPermissionsScreen
import com.aliernfrog.pftool.ui.screen.settings.SettingsDestination
import com.aliernfrog.pftool.ui.sheet.UpdateSheet
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.pftool.util.Destination
import com.aliernfrog.pftool.util.extension.removeLastIfMultiple
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()

    BaseScaffold { paddingValues ->
        NavDisplay(
            backStack = mainViewModel.navigationBackStack,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding(),
            entryProvider = entryProvider {
                entry<Destination> { destination ->
                    when (destination) {
                        Destination.MAPS -> {
                            MapsPermissionsScreen(
                                onNavigateSettingsRequest = {
                                    mainViewModel.navigationBackStack.add(SettingsDestination.ROOT)
                                }
                            )
                        }
                    }
                }

                entry<SettingsDestination>(
                    metadata = NavDisplay.transitionSpec {
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
                ) { destination ->
                    destination.content(
                        /* onNavigateBackRequest = */ {
                            mainViewModel.navigationBackStack.removeLastIfMultiple()
                        },
                        /* onNavigateRequest */ {
                            mainViewModel.navigationBackStack.add(it)
                        }
                    )
                }
            }
        )
    }

    UpdateSheet(
        sheetState = mainViewModel.updateSheetState,
        latestVersionInfo = mainViewModel.latestVersionInfo,
        updateAvailable = mainViewModel.updateAvailable,
        onCheckUpdatesRequest = { scope.launch {
            mainViewModel.checkUpdates(manuallyTriggered = true)
        } }
    )

    mainViewModel.progressState.currentProgress?.let {
        ProgressDialog(it) {}
    }
}