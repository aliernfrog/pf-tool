package com.aliernfrog.pftool

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.ui.graphics.Color
import io.github.aliernfrog.shared.data.Social
import io.github.aliernfrog.shared.impl.CreditData

const val TAG = "PFToolLogs"
const val githubRepoURL = "https://github.com/aliernfrog/pf-tool"
const val crowdinURL = "https://crowdin.com/project/pf-tool"

/**
 * Link to a releases JSON file.
 * Should return a JSON array of [io.github.aliernfrog.shared.data.ReleaseInfo].
 *
 * To automate generating such file, you can use the "/generate-releases-json.js"
 * and "/.github/workflows/generate-releases-json.yml" files, which can be found in the source code of this project.
 */
const val defaultReleasesURL = "https://raw.githubusercontent.com/aliernfrog/pf-tool/refs/heads/main/releases.json"

/**
 * Link to a crash report handler API endpoint.
 * Should listen for POST request with "app" and "details" strings in the JSON body.
 *
 * Example API source code: https://github.com/aliernfrog/proxy-api (forwards the report to a Discord webhook)
 */
const val crashReportURL = "https://aliernfrog.vercel.app/crash-report"

object SettingsConstant {
    val socials = listOf(
        Social(
            label = "Polyfield",
            icon = io.github.aliernfrog.shared.R.drawable.discord,
            iconContainerColor = Color(0xFF5865F2),
            url = "https://discord.gg/X6WzGpCgDJ"
        ),
        Social(
            label = "PF Tool",
            icon = io.github.aliernfrog.shared.R.drawable.github,
            iconContainerColor = Color(0xFF104C35),
            url = githubRepoURL
        ),
        Social(
            label = "Crowdin",
            icon = Icons.Default.Translate,
            iconContainerColor = Color(0xFF263238),
            url = crowdinURL
        )
    )

    val supportLinks = listOf(
        Social(
            label = R.string.settings_about_issues_discord,
            icon = io.github.aliernfrog.shared.R.drawable.discord,
            url = "https://discord.gg/SQXqBMs"
        ),
        Social(
            label = R.string.settings_about_issues_githubIssues,
            icon = io.github.aliernfrog.shared.R.drawable.github,
            url = "$githubRepoURL/issues"
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