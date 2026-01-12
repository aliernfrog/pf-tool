package io.github.aliernfrog.pftool_shared.impl

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.LocaleListCompat
import io.github.aliernfrog.pftool_shared.data.Language
import io.github.aliernfrog.pftool_shared.data.getAvailableLanguage
import io.github.aliernfrog.pftool_shared.util.extension.toLanguage
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.pftool_shared.util.supportsPerAppLanguagePreferences
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import kotlinx.coroutines.runBlocking

class LocaleManager(
    val languageCodes: Array<String>,
    val translationProgresses: FloatArray,
    val languagePref: () -> BasePreferenceManager.Preference<String>,
    baseLanguageCode: String = "en-US",
    context: Context
) {
    val languages = languageCodes.sorted().map { langCode ->
        PFToolSharedUtil.getLanguageFromCode(
            code = langCode,
            languages = languageCodes,
            translationProgresses = translationProgresses
        )!!
    }

    val baseLanguage = PFToolSharedUtil.getLanguageFromCode(
        code = baseLanguageCode,
        languages = languageCodes,
        translationProgresses = translationProgresses
    )!!

    val deviceLanguage = LocaleManagerCompat.getSystemLocales(context)[0]?.toLanguage(
        languages = languageCodes,
        translationProgresses = translationProgresses
    ) ?: baseLanguage

    private var _appLanguage by mutableStateOf<Language?>(null)
    var appLanguage: Language?
        get() = _appLanguage ?: deviceLanguage.getAvailableLanguage(languages) ?: baseLanguage
        set(language) {
            languagePref().value = language?.fullCode ?: ""
            val localeListCompat = if (language == null) LocaleListCompat.getEmptyLocaleList()
            else LocaleListCompat.forLanguageTags(language.languageCode)
            AppCompatDelegate.setApplicationLocales(localeListCompat)
            _appLanguage = language?.getAvailableLanguage(languages)
        }

    init {
        val lang = languagePref().value
        if (!supportsPerAppLanguagePreferences && lang.isNotBlank()) runBlocking {
            appLanguage = PFToolSharedUtil.getLanguageFromCode(
                code = lang,
                languages = languageCodes,
                translationProgresses = translationProgresses
            )?.getAvailableLanguage(languages)
        }
    }
}