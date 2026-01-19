package io.github.aliernfrog.shared.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

/**
 * Contains shared strings.
 */
open class SharedString(
    /**
     * Shared key to the string resource id.
     */
    open val key: String,

    /**
     * Resource id of the string. This can vary between apps. When this is specified, [key] will be ignored.
     */
    open val resId: Int? = null
) {
    data object AppName : SharedString("app_name")
    data object ActionBack : SharedString("action_back")
    data object ActionCancel : SharedString("action_cancel")
    data object ActionClose : SharedString("action_close")
    data object ActionDelete : SharedString("action_delete")
    data object ActionDismiss : SharedString("action_dismiss")
    data object ActionOK : SharedString("action_ok")
    data object ActionOpenInBrowser : SharedString("action_openInBrowser")
    data object InfoDeleteQuestion : SharedString("info_deleteQuestion")
    data object InfoPleaseWait : SharedString("info_pleaseWait")
    data object Warning : SharedString("warning")
    data object WarningError : SharedString("warning_error")
    data object MediaOverlayGuide : SharedString("mediaOverlay_guide")
    data object MediaOverlayGuideToggleOverlay : SharedString("mediaOverlay_guide_toggleOverlay")
    data object MediaOverlayGuideToggleZoom : SharedString("mediaOverlay_guide_toggleZoom")
    data object MediaOverlayGuideZoom : SharedString("mediaOverlay_guide_zoom")
    data object Settings : SharedString("settings")
    data object SettingsAbout : SharedString("settings_about")
    data object SettingsAboutDescription : SharedString("settings_about_description")
    data object SettingsAboutUpdates : SharedString("settings_about_updates")
    data object SettingsAboutUpdatesAutoCheckUpdates : SharedString("settings_about_updates_autoCheckUpdates")
    data object SettingsAboutUpdatesAutoCheckUpdatesDescription : SharedString("settings_about_updates_autoCheckUpdates_description")
    data object SettingsAboutIssues : SharedString("settings_about_issues")
    data object SettingsAboutIssuesTitle : SharedString("settings_about_issues_title")
    data object SettingsAboutIssuesDescription : SharedString("settings_about_issues_description")
    data object SettingsAboutIssuesCopyDebugInfo : SharedString("settings_about_issues_copyDebugInfo")
    data object SettingsAboutIssuesCopyDebugInfoDescription : SharedString("settings_about_issues_copyDebugInfo_description")
    data object SettingsAboutIssuesCopyDebugInfoClipLabel : SharedString("settings_about_issues_copyDebugInfo_clipLabel")
    data object SettingsAboutIssuesCopyDebugInfoCopied : SharedString("settings_about_issues_copyDebugInfo_copied")
    data object SettingsAboutCredits : SharedString("settings_about_credits")
    data object SettingsAboutLibs : SharedString("settings_about_libs")
    data object SettingsAboutLibsDescription : SharedString("settings_about_libs_description")
    data object SettingsAboutLibsLicense : SharedString("settings_about_libs_license")
    data object SettingsAboutLibsWebsite : SharedString("settings_about_libs_website")
    data object SettingsAboutLibsOrganization : SharedString("settings_about_libs_organization")
    data object SettingsAboutChangelog : SharedString("settings_about_changelog")
    data object SettingsAboutUpdate : SharedString("settings_about_update")
    data object SettingsAppearance : SharedString("settings_appearance")
    data object SettingsAppearanceDescription : SharedString("settings_appearance_description")
    data object SettingsAppearanceTheme : SharedString("settings_appearance_theme")
    data object SettingsAppearanceThemeSystem : SharedString("settings_appearance_theme_system")
    data object SettingsAppearanceThemeLight : SharedString("settings_appearance_theme_light")
    data object SettingsAppearanceThemeDark : SharedString("settings_appearance_theme_dark")
    data object SettingsAppearanceColors : SharedString("settings_appearance_colors")
    data object SettingsAppearanceColorsMaterialYou : SharedString("settings_appearance_materialYou")
    data object SettingsAppearanceColorsMaterialYouDescription : SharedString("settings_appearance_materialYou_description")
    data object SettingsAppearanceColorsMaterialYouUnavailable : SharedString("settings_appearance_materialYou_unavailable")
    data object SettingsAppearanceColorsPitchBlack : SharedString("settings_appearance_pitchBlack")
    data object SettingsAppearanceColorsPitchBlackDescription : SharedString("settings_appearance_pitchBlack_description")
    data object SettingsExperimental : SharedString("settings_experimental")
    data object SettingsExperimentalDescription : SharedString("settings_experimental_description")
    data object SettingsUpdateNotificationUpdateAvailable : SharedString("settings_updateNotification_updateAvailable")
    data object SettingsUpdateNotificationDescription : SharedString("settings_updateNotification_description")
    data object Updates : SharedString("updates")
    data object UpdatesChangelog : SharedString("updates_changelog")
    data object UpdatesIncompatible : SharedString("updates_incompatible")
    data object UpdatesNoChangelog : SharedString("updates_noChangelog")
    data object UpdatesCheckUpdates : SharedString("updates_checkUpdates")
    data object UpdatesCurrentVersion : SharedString("updates_currentVersion")
    data object UpdatesPrerelease : SharedString("updates_prerelease")
    data object UpdatesUpdate : SharedString("updates_update")
    data object UpdatesUpdateAvailable : SharedString("updates_updateAvailable")

    companion object {
        fun fromResId(resId: Int): SharedString {
            return SharedString(key = "", resId = resId)
        }
    }
}

@SuppressLint("LocalContextResourcesRead", "DiscouragedApi")
@Composable
fun sharedStringResource(sharedString: SharedString): String {
    val context = LocalContext.current
    val id = rememberSaveable(sharedString) {
        context.getSharedStringResId(sharedString)
    }
    return if (id == 0) "MISSING STRING: ${sharedString.key}"
    else stringResource(id)
}

fun Context.getSharedString(sharedString: SharedString): String {
    val id = this.getSharedStringResId(sharedString)
    return if (id == 0) "MISSING STRING: ${sharedString.key}"
    else this.getString(id)
}

@SuppressLint("LocalContextResourcesRead", "DiscouragedApi")
fun Context.getSharedStringResId(sharedString: SharedString): Int {
    return sharedString.resId ?: this.resources.getIdentifier(
        sharedString.key,
        "string",
        this.packageName
    )
}
