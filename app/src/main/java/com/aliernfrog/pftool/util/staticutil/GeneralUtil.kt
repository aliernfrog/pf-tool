package com.aliernfrog.pftool.util.staticutil

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.aliernfrog.pftool.di.appModules
import com.aliernfrog.pftool.ui.activity.MainActivity
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.core.context.GlobalContext.unloadKoinModules

class GeneralUtil {
    companion object {
        fun restartApp(context: Context, withModules: Boolean = true) {
            val intent = Intent(context, MainActivity::class.java)
            (context as Activity).finish()
            if (withModules) {
                unloadKoinModules(appModules)
                loadKoinModules(appModules)
            }
            context.startActivity(intent)
        }
    }
}