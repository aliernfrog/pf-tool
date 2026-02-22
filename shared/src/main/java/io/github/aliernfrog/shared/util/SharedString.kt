package io.github.aliernfrog.shared.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.stringResource
import io.github.aliernfrog.shared.di.getKoinInstance
import kotlin.reflect.KProperty1

data class SharedString(
    @StringRes val appName : Int,
    @StringRes val actionBack : Int,
    @StringRes val actionCancel : Int,
    @StringRes val actionClose : Int,
    @StringRes val actionDelete : Int,
    @StringRes val actionDismiss : Int,
    @StringRes val actionOK : Int,
    @StringRes val actionOpenInBrowser : Int,
    @StringRes val actionExpand : Int,
    @StringRes val actionCollapse : Int,
    @StringRes val infoDeleteQuestion : Int,
    @StringRes val infoPleaseWait : Int,
    @StringRes val warning : Int,
    @StringRes val warningError : Int,
    @StringRes val warningErrorTapToReport : Int,

    @StringRes val mediaOverlayGuide : Int,
    @StringRes val mediaOverlayGuideToggleOverlay : Int,
    @StringRes val mediaOverlayGuideToggleZoom : Int,
    @StringRes val mediaOverlayGuideZoom : Int,

    @StringRes val settings : Int,
    @StringRes val settingsAbout : Int,
    @StringRes val settingsAboutDescription : Int,
    @StringRes val settingsAboutUpdates : Int,
    @StringRes val settingsAboutUpdatesAutoCheckUpdates : Int,
    @StringRes val settingsAboutUpdatesAutoCheckUpdatesDescription : Int,
    @StringRes val settingsAboutIssues : Int,
    @StringRes val settingsAboutIssuesTitle : Int,
    @StringRes val settingsAboutIssuesDescription : Int,
    @StringRes val settingsAboutIssuesCopyDebugInfo : Int,
    @StringRes val settingsAboutIssuesCopyDebugInfoDescription : Int,
    @StringRes val settingsAboutIssuesCopyDebugInfoClipLabel : Int,
    @StringRes val settingsAboutIssuesCopyDebugInfoCopied : Int,
    @StringRes val settingsAboutCredits : Int,
    @StringRes val settingsAboutLibs : Int,
    @StringRes val settingsAboutLibsDescription : Int,
    @StringRes val settingsAboutLibsLicense : Int,
    @StringRes val settingsAboutLibsWebsite : Int,
    @StringRes val settingsAboutLibsOrganization : Int,
    @StringRes val settingsAboutChangelog : Int,
    @StringRes val settingsAboutUpdate : Int,
    @StringRes val settingsAppearance : Int,
    @StringRes val settingsAppearanceDescription : Int,
    @StringRes val settingsAppearanceTheme : Int,
    @StringRes val settingsAppearanceThemeSystem : Int,
    @StringRes val settingsAppearanceThemeLight : Int,
    @StringRes val settingsAppearanceThemeDark : Int,
    @StringRes val settingsAppearanceColors : Int,
    @StringRes val settingsAppearanceColorsMaterialYou : Int,
    @StringRes val settingsAppearanceColorsMaterialYouDescription : Int,
    @StringRes val settingsAppearanceColorsMaterialYouUnavailable : Int,
    @StringRes val settingsAppearanceColorsPitchBlack : Int,
    @StringRes val settingsAppearanceColorsPitchBlackDescription : Int,
    @StringRes val settingsExperimental : Int,
    @StringRes val settingsExperimentalDescription : Int,
    @StringRes val settingsUpdateNotificationUpdateAvailable : Int,
    @StringRes val settingsUpdateNotificationDescription : Int,

    @StringRes val updates : Int,
    @StringRes val updatesChangelog : Int,
    @StringRes val updatesIncompatible : Int,
    @StringRes val updatesNoChangelog : Int,
    @StringRes val updatesCheckUpdates : Int,
    @StringRes val updatesCurrentVersion : Int,
    @StringRes val updatesPrerelease : Int,
    @StringRes val updatesUpdate : Int,
    @StringRes val updatesUpdateAvailable : Int,

    @StringRes val crashHandlerTitle : Int,
    @StringRes val crashHandlerDescription : Int,
    @StringRes val crashHandlerStackTrace : Int,
    @StringRes val crashHandlerReport : Int,
    @StringRes val crashHandlerSendReport : Int,
    @StringRes val crashHandlerSendReportSent : Int,
    @StringRes val crashHandlerRestartApp : Int,
    @StringRes val crashHandlerSupport : Int,
    @StringRes val crashHandlerSupportDescription : Int,
    @StringRes val crashHandlerSupportGuide : Int,
    @StringRes val crashHandlerSupportCopyDetails : Int,
)

val LocalSharedString = staticCompositionLocalOf<SharedString> {
    error("No SharedString class provided!")
}

@Composable
fun sharedStringResource(property: KProperty1<SharedString, Int>): String {
    val provider = LocalSharedString.current
    return stringResource(property.get(provider))
}

fun Context.getSharedString(property: KProperty1<SharedString, Int>): String {
    val provider = getKoinInstance<SharedString>()
    return getString(property.get(provider))
}

sealed interface SharedStringResolvable {
    data class Property(val property: KProperty1<SharedString, Int>) : SharedStringResolvable
    data class Resource(@StringRes val id: Int): SharedStringResolvable
    data class Raw(val string: String) : SharedStringResolvable
}

@Composable
fun SharedStringResolvable.resolve(): String {
    return when (this) {
        is SharedStringResolvable.Property -> sharedStringResource(property)
        is SharedStringResolvable.Resource -> stringResource(id)
        is SharedStringResolvable.Raw -> string
    }
}

fun SharedStringResolvable.resolve(context: Context): String {
    return when (this) {
        is SharedStringResolvable.Property -> context.getSharedString(property)
        is SharedStringResolvable.Resource -> context.getString(id)
        is SharedStringResolvable.Raw -> string
    }
}