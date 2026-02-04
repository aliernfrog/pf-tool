package com.aliernfrog.pftool

import android.app.Application
import com.aliernfrog.pftool.di.appModules
import com.aliernfrog.pftool.ui.activity.CrashHandlerActivity
import io.github.aliernfrog.shared.di.getKoinInstance
import io.github.aliernfrog.shared.impl.VersionManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import kotlin.system.exitProcess

class PFToolApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            CrashHandlerActivity.start(
                context = this,
                throwable = throwable,
                debugInfo = getKoinInstance<VersionManager>().getDebugInfo()
            )
            exitProcess(1)
        }

        startKoin {
            androidContext(this@PFToolApplication)
            modules(appModules)
        }
    }
}