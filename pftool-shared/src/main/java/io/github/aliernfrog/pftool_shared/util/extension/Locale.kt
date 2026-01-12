package io.github.aliernfrog.pftool_shared.util.extension

import io.github.aliernfrog.pftool_shared.data.Language
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import java.util.Locale

val Locale.fullCode: String
    get() = "$language-$country"

fun Locale.toLanguage(languages: Array<String>, translationProgresses: FloatArray): Language? {
    return PFToolSharedUtil.getLanguageFromCode(
        code = fullCode,
        languages, translationProgresses
    )
}