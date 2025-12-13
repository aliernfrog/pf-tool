package com.aliernfrog.pftool.util.staticutil

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.aliernfrog.pftool.BuildConfig
import com.aliernfrog.pftool.di.appModules
import com.aliernfrog.pftool.ui.activity.MainActivity
import io.github.aliernfrog.pftool_shared.data.Language
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.core.context.GlobalContext.unloadKoinModules

class GeneralUtil {
    companion object {
        /**
         * Gets [Language] from given language code.
         * [code] must either be a language code, or language and country code splitted by a "-" (e.g.: en-US, en)
         *
         * @return [Language] if [code] is valid, null if it is invalid
         */
        fun getLanguageFromCode(code: String): Language? =
            PFToolSharedUtil.getLanguageFromCode(
                code = code,
                languages = BuildConfig.LANGUAGES,
                translationProgresses = BuildConfig.TRANSLATION_PROGRESSES
            )

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