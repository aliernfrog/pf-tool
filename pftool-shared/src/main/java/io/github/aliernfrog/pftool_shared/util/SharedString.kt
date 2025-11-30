package io.github.aliernfrog.pftool_shared.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

enum class SharedString(val key: String) {
    ACTION_CANCEL("action_cancel"),
    ACTION_DELETE("action_delete"),
    ACTION_DISMISS("action_dismiss"),
    INFO_DELETE_QUESTION("info_deleteQuestion"),
    INFO_PLEASE_WAIT("info_pleaseWait"),
    LIST_SORTING("list_sorting"),
    LIST_SORTING_NAME("list_sorting_name"),
    LIST_SORTING_DATE("list_sorting_date"),
    LIST_SORTING_SIZE("list_sorting_size"),
    LIST_SORTING_REVERSED("list_sorting_reversed"),
    LIST_STYLE("list_style"),
    LIST_STYLE_LIST("list_style_list"),
    LIST_STYLE_GRID("list_style_grid"),
    LIST_STYLE_GRID_MAX_LINE_SPAN("list_style_gridMaxLineSpan"),
    UPDATES_CHECK_UPDATES("updates_checkUpdates"),
    UPDATES_OPEN_IN_GITHUB("updates_openInGithub"),
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