package io.github.aliernfrog.pftool_shared.data

data class Language(
    val languageCode: String,
    val countryCode: String?,
    val fullCode: String,
    val localizedName: String,
    val translationProgress: Float
)