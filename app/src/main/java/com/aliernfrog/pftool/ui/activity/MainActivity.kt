package com.aliernfrog.pftool.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.aliernfrog.pftool.SettingsConstant.supportLinks
import com.aliernfrog.pftool.crashReportURL
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.ui.screen.SettingsScreen
import com.aliernfrog.pftool.ui.screen.maps.MapsScreen
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.pftool.util.Destination
import com.aliernfrog.pftool.util.UpdateScreenDestination
import com.aliernfrog.pftool.util.extension.removeLastIfMultiple
import com.aliernfrog.pftool.util.slideTransitionMetadata
import com.aliernfrog.pftool.util.slideVerticalTransitionMetadata
import com.aliernfrog.toptoast.component.TopToastHost
import io.github.aliernfrog.pftool_shared.impl.SAFFileCreator
import io.github.aliernfrog.pftool_shared.ui.dialog.ProgressDialog
import io.github.aliernfrog.pftool_shared.util.LocalPFToolSharedString
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.shared.ui.component.MediaOverlay
import io.github.aliernfrog.shared.ui.component.util.AppContainer
import io.github.aliernfrog.shared.ui.component.util.InsetsObserver
import io.github.aliernfrog.shared.ui.screen.UpdatesScreen
import io.github.aliernfrog.shared.ui.screen.settings.SettingsDestination
import io.github.aliernfrog.shared.ui.sheet.CrashDetailsSheet
import io.github.aliernfrog.shared.ui.theme.Theme
import io.github.aliernfrog.shared.util.LocalSharedString
import io.github.aliernfrog.shared.util.SharedString
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var safZipFileCreator: SAFFileCreator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        safZipFileCreator = SAFFileCreator(this, mimeType = "application/zip")
        installSplashScreen()

        val vm = getViewModel<MainViewModel>()
        val sharedString by inject<SharedString>()
        val pfToolSharedString by inject<PFToolSharedString>()

        setContent {
            val context = LocalContext.current
            val view = LocalView.current
            val useDarkTheme = shouldUseDarkTheme(vm.prefs.theme.value)
            var isAppInitialized by rememberSaveable { mutableStateOf(false) }

            @Composable
            fun AppTheme(content: @Composable () -> Unit) {
                PFToolTheme(
                    darkTheme = useDarkTheme,
                    useLightSystemBars = !useDarkTheme && vm.mediaOverlayData == null,
                    dynamicColors = vm.prefs.materialYou.value,
                    pitchBlack = vm.prefs.pitchBlack.value,
                    content = content
                )
            }

            AppTheme {
                CompositionLocalProvider(
                    LocalSharedString provides sharedString,
                    LocalPFToolSharedString provides pfToolSharedString
                ) {
                    App(vm)
                }
            }

            LaunchedEffect(Unit) {
                vm.setSafZipFileCreator(safZipFileCreator)
                vm.topToastState.setComposeView(view)
                if (isAppInitialized) return@LaunchedEffect

                vm.topToastState.setAppTheme { AppTheme(it) }
                this@MainActivity.intent?.let {
                    vm.handleIntent(it, context = context)
                }
                isAppInitialized = true
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun App(vm: MainViewModel) {
        val availableUpdates = vm.availableUpdates.collectAsStateWithLifecycle().value
        val currentVersionInfo = vm.currentVersionInfo.collectAsStateWithLifecycle().value
        val isCompatibleWithLatestVersion = vm.isCompatibleWithLatestVersion.collectAsStateWithLifecycle().value
        val isCheckingForUpdates = vm.isCheckingForUpdates.collectAsStateWithLifecycle().value

        val onNavigateBackRequest: () -> Unit = {
            vm.navigationBackStack.removeLastIfMultiple()
        }

        InsetsObserver()

        AppContainer {
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

            CrashDetailsSheet(
                throwable = vm.lastCaughtException,
                crashReportURL = crashReportURL,
                debugInfo = vm.versionManager.getDebugInfo(),
                supportLinks = supportLinks
            )

            vm.progressState.currentProgress?.let {
                ProgressDialog(it) {
                    vm.progressState.currentProgress = null
                }
            }

            Crossfade(vm.mediaOverlayData) { data ->
                if (data != null) MediaOverlay(
                    data = data,
                    showMediaOverlayGuidePref = vm.prefs.showMediaOverlayGuide,
                    onDismissRequest = { vm.dismissMediaOverlay() }
                )
            }
            TopToastHost(vm.topToastState)
        }
    }

    @Composable
    private fun shouldUseDarkTheme(theme: Int): Boolean {
        return when(theme) {
            Theme.LIGHT.ordinal -> false
            Theme.DARK.ordinal -> true
            else -> isSystemInDarkTheme()
        }
    }
}