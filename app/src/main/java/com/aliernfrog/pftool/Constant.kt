package com.aliernfrog.pftool

import android.os.Build
import android.os.Environment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import com.aliernfrog.pftool.data.PrefEditItem
import com.aliernfrog.pftool.data.Social
import com.aliernfrog.pftool.impl.CreditData
import com.aliernfrog.pftool.util.staticutil.GeneralUtil

const val TAG = "PFToolLogs"

const val experimentalSettingsRequiredClicks = 10
const val githubRepoURL = "https://github.com/aliernfrog/pf-tool"
const val crowdinURL = "https://crowdin.com/project/pf-tool"

val externalStorageRoot = Environment.getExternalStorageDirectory().toString()+"/"
val supportsPerAppLanguagePreferences = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
val folderPickerSupportsInitialUri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
val hasAndroidDataRestrictions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

object SettingsConstant {
    val folders = listOf(
        PrefEditItem(
            preference = { it.pfMapsDir },
            label = { R.string.settings_storage_folders_maps }
        ),
        PrefEditItem(
            preference = { it.exportedMapsDir },
            label = { R.string.settings_storage_folders_exportedMaps }
        )
    )

    val socials = listOf(
        Social(
            label = "Polyfield",
            icon = R.drawable.discord,
            url = "https://discord.gg/X6WzGpCgDJ"
        ),
        Social(
            label = "PF Tool",
            icon = R.drawable.github,
            url = githubRepoURL
        ),
        Social(
            label = "Crowdin",
            icon = Icons.Default.Translate,
            url = crowdinURL
        )
    )

    val credits = listOf(
        CreditData(
            name = "Mohammad Alizadeh",
            githubUsername = "Alizadev",
            description = R.string.settings_about_credits_pfDev,
            link = "https://discord.gg/X6WzGpCgDJ"
        ),
        CreditData(
            name = "alieRN",
            githubUsername = "aliernfrog",
            description = R.string.settings_about_credits_pfToolDev
        ),
        CreditData(
            name = "infini0083",
            githubUsername = "infini0083",
            description = R.string.settings_about_credits_ui
        ),
        CreditData(
            name = R.string.settings_about_credits_crowdin,
            githubUsername = "crowdin",
            description = R.string.settings_about_credits_translations,
            link = "https://crowdin.com/project/pf-tool"
        ),
        CreditData(
            name = "Vendetta Manager",
            githubUsername = "vendetta-mod",
            description = R.string.settings_about_credits_inspiration,
            link = "https://github.com/vendetta-mod/VendettaManager"
        ),
        CreditData(
            name = "ReVanced Manager",
            githubUsername = "ReVanced",
            description = R.string.settings_about_credits_inspiration,
            link = "https://github.com/ReVanced/revanced-manager"
        )
    )
}

val languages = BuildConfig.LANGUAGES.sorted().map { langCode ->
    GeneralUtil.getLanguageFromCode(langCode)!!
}