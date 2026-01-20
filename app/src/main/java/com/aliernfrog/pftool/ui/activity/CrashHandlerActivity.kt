package com.aliernfrog.pftool.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aliernfrog.pftool.SettingsConstant.supportLinks
import com.aliernfrog.pftool.ui.theme.PFToolTheme
import io.github.aliernfrog.shared.ui.component.util.AppContainer
import io.github.aliernfrog.shared.ui.screen.CrashHandlerScreen

class CrashHandlerActivity : ComponentActivity() {
    companion object {
        private const val EXTRA_CRASH_MESSAGE = "EXTRA_CRASH_MESSAGE"
        private const val EXTRA_CRASH_STACKTRACE = "EXTRA_CRASH_STACKTRACE"

        fun start(context: Context, throwable: Throwable) {
            val intent = Intent(context, CrashHandlerActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(EXTRA_CRASH_MESSAGE, throwable.toString())
                .putExtra(EXTRA_CRASH_STACKTRACE, throwable.stackTraceToString())
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashMessage = intent.getStringExtra(EXTRA_CRASH_MESSAGE)
            ?: return
        val crashStackTrace = intent.getStringExtra(EXTRA_CRASH_STACKTRACE)
            ?: return

        setContent {
            PFToolTheme {
                AppContainer {
                    CrashHandlerScreen(
                        message = crashMessage,
                        stackTrace = crashStackTrace,
                        supportLinks = supportLinks
                    )
                }
            }
        }
    }
}