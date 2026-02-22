package com.aliernfrog.pftool.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.aliernfrog.pftool.BuildConfig
import com.aliernfrog.pftool.SettingsConstant.supportLinks
import com.aliernfrog.pftool.crashReportURL
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import com.aliernfrog.pftool.util.pfToolSharedString
import com.aliernfrog.pftool.util.sharedString
import com.aliernfrog.pftool.util.staticutil.GeneralUtil
import io.github.aliernfrog.pftool_shared.util.LocalPFToolSharedString
import io.github.aliernfrog.shared.ui.component.util.AppContainer
import io.github.aliernfrog.shared.ui.screen.CrashHandlerScreen
import io.github.aliernfrog.shared.util.LocalSharedString

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
                CompositionLocalProvider(
                    LocalSharedString provides sharedString,
                    LocalPFToolSharedString provides pfToolSharedString
                ) {
                    AppContainer {
                        CrashHandlerScreen(
                            crashReportURL = crashReportURL,
                            stackTrace = crashStackTrace,
                            debugInfo = debugInfo,
                            supportLinks = supportLinks,
                            onRestartAppRequest = {
                                GeneralUtil.restartApp(this@CrashHandlerActivity)
                            }
                        )
                    }
                }
            }
        }
    }
}