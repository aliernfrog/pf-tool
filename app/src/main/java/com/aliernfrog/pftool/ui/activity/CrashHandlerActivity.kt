package com.aliernfrog.pftool.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aliernfrog.pftool.BuildConfig
import com.aliernfrog.pftool.SettingsConstant.supportLinks
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import io.github.aliernfrog.shared.ui.component.util.AppContainer
import io.github.aliernfrog.shared.ui.screen.CrashHandlerScreen

class CrashHandlerActivity : ComponentActivity() {
    companion object {
        private const val EXTRA_CRASH_MESSAGE = "EXTRA_CRASH_MESSAGE"
        private const val EXTRA_CRASH_STACKTRACE = "EXTRA_CRASH_STACKTRACE"
        private const val EXTRA_DEBUG_INFO = "EXTRA_DEBUG_INFO"

        fun start(context: Context, throwable: Throwable, debugInfo: String) {
            val intent = Intent(context, CrashHandlerActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(EXTRA_CRASH_MESSAGE, throwable.toString())
                .putExtra(EXTRA_CRASH_STACKTRACE, throwable.stackTraceToString())
                .putExtra(EXTRA_DEBUG_INFO, debugInfo)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashStackTrace = intent.getStringExtra(EXTRA_CRASH_STACKTRACE)
            ?: return

        @Suppress("KotlinConstantConditions")
        val debugInfo = intent.getStringExtra(EXTRA_DEBUG_INFO)
            ?: "Android SDK ${Build.VERSION.SDK_INT}, commit ${BuildConfig.GIT_COMMIT} ${
                if (BuildConfig.GIT_LOCAL_CHANGES) "(has local changes)" else ""
            }"

        setContent {
            PFToolTheme {
                AppContainer {
                    CrashHandlerScreen(
                        stackTrace = crashStackTrace,
                        debugInfo = debugInfo,
                        supportLinks = supportLinks
                    )
                }
            }
        }
    }
}