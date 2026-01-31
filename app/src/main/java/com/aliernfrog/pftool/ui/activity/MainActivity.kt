package com.aliernfrog.pftool.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aliernfrog.pftool.ui.screen.MainScreen
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.toptoast.component.TopToastHost
import io.github.aliernfrog.pftool_shared.impl.SAFFileCreator
import io.github.aliernfrog.shared.ui.component.MediaOverlay
import io.github.aliernfrog.shared.ui.component.util.AppContainer
import io.github.aliernfrog.shared.ui.component.util.InsetsObserver
import io.github.aliernfrog.shared.ui.theme.Theme
import org.koin.androidx.compose.koinViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var safZipFileCreator: SAFFileCreator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        safZipFileCreator = SAFFileCreator(this, mimeType = "application/zip")
        installSplashScreen()

        setContent {
            AppContent()
        }
    }

    @Composable
    private fun AppContent(
        mainViewModel: MainViewModel = koinViewModel()
    ) {
        val context = LocalContext.current
        val view = LocalView.current
        val useDarkTheme = shouldUseDarkTheme(mainViewModel.prefs.theme.value)
        var isAppInitialized by rememberSaveable { mutableStateOf(false) }

        @Composable
        fun AppTheme(content: @Composable () -> Unit) {
            PFToolTheme(
                darkTheme = useDarkTheme,
                useLightSystemBars = !useDarkTheme && mainViewModel.mediaOverlayData == null,
                dynamicColors = mainViewModel.prefs.materialYou.value,
                pitchBlack = mainViewModel.prefs.pitchBlack.value,
                content = content
            )
        }

        AppTheme {
            InsetsObserver()
            AppContainer {
                MainScreen()
                Crossfade(mainViewModel.mediaOverlayData) { data ->
                    if (data != null) MediaOverlay(
                        data = data,
                        showMediaOverlayGuidePref = mainViewModel.prefs.showMediaOverlayGuide,
                        onDismissRequest = { mainViewModel.dismissMediaOverlay() }
                    )
                }
                TopToastHost(mainViewModel.topToastState)
            }
        }

        LaunchedEffect(Unit) {
            mainViewModel.setSafZipFileCreator(safZipFileCreator)
            mainViewModel.topToastState.setComposeView(view)
            if (isAppInitialized) return@LaunchedEffect

            mainViewModel.topToastState.setAppTheme { AppTheme(it) }
            if (mainViewModel.prefs.autoCheckUpdates.value) mainViewModel.checkUpdates()
            this@MainActivity.intent?.let {
                mainViewModel.handleIntent(it, context = context)
            }
            isAppInitialized = true
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