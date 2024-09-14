package com.aliernfrog.pftool.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aliernfrog.pftool.ui.component.InsetsObserver
import com.aliernfrog.pftool.ui.screen.MainScreen
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.aliernfrog.pftool.ui.theme.Theme
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.toptoast.component.TopToastHost
import org.koin.androidx.compose.koinViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val scope = rememberCoroutineScope()
        val useDarkTheme = shouldUseDarkTheme(mainViewModel.prefs.theme.value)
        var isAppInitialized by rememberSaveable { mutableStateOf(false) }

        @Composable
        fun AppTheme(content: @Composable () -> Unit) {
            PFToolTheme(
                darkTheme = useDarkTheme,
                dynamicColors = mainViewModel.prefs.materialYou.value,
                pitchBlack = mainViewModel.prefs.pitchBlack.value,
                content = content
            )
        }

        AppTheme {
            InsetsObserver()
            AppContainer {
                MainScreen()
                TopToastHost(mainViewModel.topToastState)
            }
        }

        LaunchedEffect(Unit) {
            mainViewModel.scope = scope
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
    private fun AppContainer(
        content: @Composable BoxScope.() -> Unit
    ) {
        val config = LocalConfiguration.current
        var modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
            modifier = modifier
                .displayCutoutPadding()
                .navigationBarsPadding()

        Box(
            modifier = modifier,
            content = content
        )
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