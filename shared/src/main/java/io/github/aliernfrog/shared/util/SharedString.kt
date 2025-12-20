package io.github.aliernfrog.shared.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

open class SharedString(open val key: String) {
    data object AppName : SharedString("app_name")
    data object ActionBack : SharedString("action_back")
    data object ActionCancel : SharedString("action_cancel")
    data object ActionClose : SharedString("action_close")
    data object ActionDelete : SharedString("action_delete")
    data object ActionDismiss : SharedString("action_dismiss")
    data object ActionOK : SharedString("action_ok")
    data object InfoDeleteQuestion : SharedString("info_deleteQuestion")
    data object InfoPleaseWait : SharedString("info_pleaseWait")
    data object WarningError : SharedString("warning_error")
    data object MediaOverlayGuide : SharedString("mediaOverlay_guide")
    data object MediaOverlayGuideToggleOverlay : SharedString("mediaOverlay_guide_toggleOverlay")
    data object MediaOverlayGuideToggleZoom : SharedString("mediaOverlay_guide_toggleZoom")
    data object MediaOverlayGuideZoom : SharedString("mediaOverlay_guide_zoom")
    data object Settings : SharedString("settings")
    data object SettingsAbout : SharedString("settings_about")
    data object SettingsAboutUpdates : SharedString("settings_about_updates")
    data object SettingsAboutUpdatesAutoCheckUpdates : SharedString("settings_about_updates_autoCheckUpdates")
    data object SettingsAboutUpdatesAutoCheckUpdatesDescription : SharedString("settings_about_updates_autoCheckUpdates_description")
    data object SettingsAboutCredits : SharedString("settings_about_credits")
    data object SettingsAboutLibs : SharedString("settings_about_libs")
    data object SettingsAboutLibsDescription : SharedString("settings_about_libs_description")
    data object SettingsAboutLibsLicense : SharedString("settings_about_libs_license")
    data object SettingsAboutLibsWebsite : SharedString("settings_about_libs_website")
    data object SettingsAboutLibsOrganization : SharedString("settings_about_libs_organization")
    data object SettingsAboutOther : SharedString("settings_about_other")
    data object SettingsAboutOtherCopyDebugInfo : SharedString("settings_about_other_copyDebugInfo")
    data object SettingsAboutOtherCopyDebugInfoDescription : SharedString("settings_about_other_copyDebugInfo_description")
    data object SettingsAboutOtherCopyDebugInfoClipLabel : SharedString("settings_about_other_copyDebugInfo_clipLabel")
    data object SettingsAboutOtherCopyDebugInfoCopied : SharedString("settings_about_other_copyDebugInfo_copied")
    data object SettingsAboutChangelog : SharedString("settings_about_changelog")
    data object SettingsAboutUpdate : SharedString("settings_about_update")
    data object SettingsAppearance : SharedString("settings_appearance")
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
    data object UpdatesCheckUpdates : SharedString("updates_checkUpdates")
    data object UpdatesUpdateAvailable : SharedString("updates_updateAvailable")
    data object UpdatesOpenInGithub : SharedString("updates_openInGithub")
    data object UpdatesNoChangelog : SharedString("updates_noChangelog")
    data object UpdatesPrerelease : SharedString("updates_preRelease")
    data object UpdatesStable : SharedString("updates_stable")
    data object UpdatesUpdate : SharedString("updates_update")
}

@SuppressLint("LocalContextResourcesRead", "DiscouragedApi")
@Composable
fun sharedStringResource(sharedString: SharedString): String {
    val context = LocalContext.current
    val id = remember {
        context.resources.getIdentifier(
            sharedString.key,
            "string",
            context.packageName
        )
    }
    return if (id == 0) "MISSING STRING: ${sharedString.key}"
    else stringResource(id)
}

@SuppressLint("LocalContextResourcesRead", "DiscouragedApi")
fun Context.getSharedString(sharedString: SharedString): String {
    val id = this.resources.getIdentifier(
        sharedString.key,
        "string",
        this.packageName
    )
    return if (id == 0) "MISSING STRING: ${sharedString.key}"
    else this.getString(id)
}