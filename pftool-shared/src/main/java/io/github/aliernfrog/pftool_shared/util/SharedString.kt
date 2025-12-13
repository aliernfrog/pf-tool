package io.github.aliernfrog.pftool_shared.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

enum class SharedString(val key: String) {
    APP_NAME("app_name"),
    ACTION_BACK("action_back"),
    ACTION_CANCEL("action_cancel"),
    ACTION_CLOSE("action_close"),
    ACTION_DELETE("action_delete"),
    ACTION_DISMISS("action_dismiss"),
    ACTION_OK("action_ok"),
    INFO_DELETE_QUESTION("info_deleteQuestion"),
    INFO_PLEASE_WAIT("info_pleaseWait"),
    WARNING_ERROR("warning_error"),
    LIST_SORTING("list_sorting"),
    LIST_SORTING_NAME("list_sorting_name"),
    LIST_SORTING_DATE("list_sorting_date"),
    LIST_SORTING_SIZE("list_sorting_size"),
    LIST_SORTING_REVERSED("list_sorting_reversed"),
    LIST_STYLE("list_style"),
    LIST_STYLE_LIST("list_style_list"),
    LIST_STYLE_GRID("list_style_grid"),
    LIST_STYLE_GRID_MAX_LINE_SPAN("list_style_gridMaxLineSpan"),
    MEDIA_OVERLAY_GUIDE("mediaOverlay_guide"),
    MEDIA_OVERLAY_GUIDE_TOGGLE_OVERLAY("mediaOverlay_guide_toggleOverlay"),
    MEDIA_OVERLAY_GUIDE_TOGGLE_ZOOM("mediaOverlay_guide_toggleZoom"),
    MEDIA_OVERLAY_GUIDE_ZOOM("mediaOverlay_guide_zoom"),
    SETTINGS("settings"),
    SETTINGS_ABOUT("settings_about"),
    SETTINGS_ABOUT_UPDATES("settings_about_updates"),
    SETTINGS_ABOUT_UPDATES_AUTO_CHECK_UPDATES("settings_about_updates_autoCheckUpdates"),
    SETTINGS_ABOUT_UPDATES_AUTO_CHECK_UPDATES_DESCRIPTION("settings_about_updates_autoCheckUpdates_description"),
    SETTINGS_ABOUT_CREDITS("settings_about_credits"),
    SETTINGS_ABOUT_LIBS("settings_about_libs"),
    SETTINGS_ABOUT_LIBS_DESCRIPTION("settings_about_libs_description"),
    SETTINGS_ABOUT_LIBS_LICENSE("settings_about_libs_license"),
    SETTINGS_ABOUT_LIBS_WEBSITE("settings_about_libs_website"),
    SETTINGS_ABOUT_LIBS_ORGANIZATION("settings_about_libs_organization"),
    SETTINGS_ABOUT_OTHER("settings_about_other"),
    SETTINGS_ABOUT_OTHER_COPY_DEBUG_INFO("settings_about_other_copyDebugInfo"),
    SETTINGS_ABOUT_OTHER_COPY_DEBUG_INFO_DESCRIPTION("settings_about_other_copyDebugInfo_description"),
    SETTINGS_ABOUT_OTHER_COPY_DEBUG_INFO_CLIP_LABEL("settings_about_other_copyDebugInfo_clipLabel"),
    SETTINGS_ABOUT_OTHER_COPY_DEBUG_INFO_COPIED("settings_about_other_copyDebugInfo_copied"),
    SETTINGS_ABOUT_CHANGELOG("settings_about_changelog"),
    SETTINGS_ABOUT_UPDATE("settings_about_update"),
    SETTINGS_APPEARANCE("settings_appearance"),
    SETTINGS_APPEARANCE_THEME("settings_appearance_theme"),
    SETTINGS_APPEARANCE_THEME_SYSTEM("settings_appearance_theme_system"),
    SETTINGS_APPEARANCE_THEME_LIGHT("settings_appearance_theme_light"),
    SETTINGS_APPEARANCE_THEME_DARK("settings_appearance_theme_dark"),
    SETTINGS_APPEARANCE_COLORS("settings_appearance_colors"),
    SETTINGS_APPEARANCE_COLORS_MATERIAL_YOU("settings_appearance_materialYou"),
    SETTINGS_APPEARANCE_COLORS_MATERIAL_YOU_DESCRIPTION("settings_appearance_materialYou_description"),
    SETTINGS_APPEARANCE_COLORS_MATERIAL_YOU_UNAVAILABLE("settings_appearance_materialYou_unavailable"),
    SETTINGS_APPEARANCE_COLORS_PITCH_BLACK("settings_appearance_pitchBlack"),
    SETTINGS_APPEARANCE_COLORS_PITCH_BLACK_DESCRIPTION("settings_appearance_pitchBlack_description"),
    SETTINGS_EXPERIMENTAL("settings_experimental"),
    SETTINGS_EXPERIMENTAL_DESCRIPTION("settings_experimental_description"),
    SETTINGS_LANGUAGE("settings_language"),
    SETTINGS_LANGUAGE_SYSTEM("settings_language_system"),
    SETTINGS_LANGUAGE_SYSTEM_FOLLOW("settings_language_system_follow"),
    SETTINGS_LANGUAGE_SYSTEM_NOT_AVAILABLE("settings_language_system_notAvailable"),
    SETTINGS_LANGUAGE_OTHER("settings_language_other"),
    SETTINGS_LANGUAGE_PROGRESS_BASE("settings_language_progress_base"),
    SETTINGS_LANGUAGE_PROGRESS_PERCENT("settings_language_progress_percent"),
    SETTINGS_LANGUAGE_HELP("settings_language_help"),
    SETTINGS_LANGUAGE_HELP_DESCRIPTION("settings_language_help_description"),
    SETTINGS_LANGUAGE_HELP_DEVICE_NOT_AVAILABLE("settings_language_help_deviceNotAvailable"),
    SETTINGS_MAPS("settings_maps"),
    SETTINGS_MAPS_THUMBNAILS("settings_maps_thumbnails"),
    SETTINGS_MAPS_THUMBNAILS_CHOSEN("settings_maps_thumbnails_chosen"),
    SETTINGS_MAPS_THUMBNAILS_CHOSEN_DESCRIPTION("settings_maps_thumbnails_chosen_description"),
    SETTINGS_MAPS_THUMBNAILS_LIST("settings_maps_thumbnails_list"),
    SETTINGS_MAPS_THUMBNAILS_LIST_DESCRIPTION("settings_maps_thumbnails_list_description"),
    SETTINGS_MAPS_THUMBNAILS_BEHAVIOR("settings_maps_behavior"),
    SETTINGS_MAPS_THUMBNAILS_BEHAVIOR_STACKUP("settings_maps_behavior_stackup"),
    SETTINGS_MAPS_THUMBNAILS_BEHAVIOR_STACKUP_DESCRIPTION("settings_maps_behavior_stackup_description"),
    SETTINGS_STORAGE("settings_storage"),
    SETTINGS_STORAGE_STORAGE_ACCESS_TYPE("settings_storage_storageAccessType"),
    SETTINGS_STORAGE_STORAGE_ACCESS_TYPE_SAF("settings_storage_storageAccessType_saf"),
    SETTINGS_STORAGE_STORAGE_ACCESS_TYPE_SAF_DESCRIPTION("settings_storage_storageAccessType_saf_description"),
    SETTINGS_STORAGE_STORAGE_ACCESS_TYPE_SHIZUKU("settings_storage_storageAccessType_shizuku"),
    SETTINGS_STORAGE_STORAGE_ACCESS_TYPE_SHIZUKU_DESCRIPTION("settings_storage_storageAccessType_shizuku_description"),
    SETTINGS_STORAGE_STORAGE_ACCESS_TYPE_ALL_FILES("settings_storage_storageAccessType_allFiles"),
    SETTINGS_STORAGE_STORAGE_ACCESS_TYPE_ALL_FILES_DESCRIPTION("settings_storage_storageAccessType_allFiles_description"),
    SETTINGS_STORAGE_FOLDERS("settings_storage_folders"),
    SETTINGS_STORAGE_FOLDERS_CHOOSE("settings_storage_folders_choose"),
    SETTINGS_STORAGE_FOLDERS_RESTORE_DEFAULT("settings_storage_folders_restoreDefault"),
    SETTINGS_STORAGE_FOLDERS_RECOMMENDED_FOLDER("settings_storage_folders_recommendedFolder"),
    SETTINGS_STORAGE_FOLDERS_NOT_SET("settings_storage_folders_notSet"),
    SETTINGS_STORAGE_FOLDERS_OPEN_CURRENT("settings_storage_folders_openCurrent"),
    SETTINGS_STORAGE_FOLDERS_OPEN_RECOMMENDED("settings_storage_folders_openRecommended"),
    SETTINGS_STORAGE_FOLDERS_OPEN_ANDROID_DATA("settings_storage_folders_openAndroidData"),
    SETTINGS_UPDATE_NOTIFICATION_UPDATE_AVAILABLE("settings_updateNotification_updateAvailable"),
    SETTINGS_UPDATE_NOTIFICATION_DESCRIPTION("settings_updateNotification_description"),
    UPDATES_CHECK_UPDATES("updates_checkUpdates"),
    UPDATES_UPDATE_AVAILABLE("updates_updateAvailable"),
    UPDATES_OPEN_IN_GITHUB("updates_openInGithub"),
    UPDATES_NO_CHANGELOG("updates_noChangelog"),
    UPDATES_PRERELEASE("updates_preRelease"),
    UPDATES_STABLE("updates_stable"),
    UPDATES_UPDATE("updates_update")
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