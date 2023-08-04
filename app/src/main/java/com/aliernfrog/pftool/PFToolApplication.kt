package com.aliernfrog.pftool

import android.app.Application
import com.aliernfrog.pftool.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PFToolApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PFToolApplication)
            modules(appModules)
        }
    }
}