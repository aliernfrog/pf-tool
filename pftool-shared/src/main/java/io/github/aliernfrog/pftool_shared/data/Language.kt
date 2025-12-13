package io.github.aliernfrog.pftool_shared.data

import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil

data class Language(
    val languageCode: String,
    val countryCode: String?,
    val fullCode: String,
    val localizedName: String,
    val translationProgress: Float
)

/**
 * Checks if the language is supported by the app and returns the [Language] if available.
 * May return a different region if the specified region does not exist.
 */
fun Language.getAvailableLanguage(languages: List<Language>): Language? {
    return languages.find {
        it.fullCode == fullCode // Same language and region
    } ?: languages.find {
        it.languageCode == languageCode // Different region, same language
    }
}

fun Language.getNameIn(language: String, country: String? = null): String {
    val locale = PFToolSharedUtil.getLocale(languageCode, countryCode)
    val inLocale = PFToolSharedUtil.getLocale(language, country)
    return locale.getDisplayName(inLocale)
}