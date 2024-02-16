package com.aliernfrog.pftool.util.extension

import com.aliernfrog.pftool.data.Language
import com.aliernfrog.pftool.util.staticutil.GeneralUtil
import java.util.Locale

val Locale.fullCode: String
    get() = "$language-$country"

fun Locale.toLanguage(): Language? {
    return GeneralUtil.getLanguageFromCode(fullCode)
}