package com.aliernfrog.pftool.util.extension

import com.aliernfrog.pftool.data.Language
import com.aliernfrog.pftool.languages
import com.aliernfrog.pftool.util.staticutil.GeneralUtil

/**
 * Checks if the language is supported by the app and returns the [Language] if available.
 * May return a different region if the specified region does not exist.
 */
fun Language.getAvailableLanguage(): Language? {
    return languages.find {
        it.fullCode == fullCode // Same language and region
    } ?: languages.find {
        it.languageCode == languageCode // Different region, same language
    }
}

fun Language.getNameIn(language: String, country: String? = null): String {
    val locale = GeneralUtil.getLocale(languageCode, countryCode)
    val inLocale = GeneralUtil.getLocale(language, country)
    return locale.getDisplayName(inLocale)
}