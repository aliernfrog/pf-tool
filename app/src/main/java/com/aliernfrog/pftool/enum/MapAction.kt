package com.aliernfrog.pftool.enum

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AddToHomeScreen
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.ui.graphics.vector.ImageVector
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.MapActionResult
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.impl.Progress
import com.aliernfrog.pftool.util.extension.showErrorToast
import com.aliernfrog.pftool.util.staticutil.FileUtil

/**
 * Contains map actions.
 */
@Suppress("unused")
enum class MapAction(
    /**
     * Resource ID to use as a short label. This can be used for one or multiple maps.
     */
    @StringRes val shortLabel: Int,

    /**
     * Resource ID to use as a long label. This should be used for only one map.
     */
    @StringRes val longLabel: Int = shortLabel,

    /**
     * Resource ID to use as a description. This should be used for only one map.
     */
    @StringRes val description: Int? = null,

    /**
     * Icon of the action.
     */
    val icon: ImageVector,

    /**
     * Whether the action is available for multi-selection.
     */
    val availableForMultiSelection: Boolean = true,

    /**
     * Whether the action is destructive.
     */
    val destructive: Boolean = false,

    /**
     * Whether the action is available for [map].
     */
    val availableFor: (map: MapFile) -> Boolean
) {
    RENAME(
        shortLabel = R.string.maps_rename,
        icon = Icons.Rounded.Edit,
        availableForMultiSelection = false,
        availableFor = {
            it.importedState != MapImportedState.NONE
        }
    ) {
        override suspend fun execute(context: Context, vararg maps: MapFile, args: List<Any>) {
            val first = maps.first()
            val newName = resolveMapNameFromArgs(args, first.name)
            first.mapsViewModel.activeProgress = Progress(
                description = context.getString(R.string.maps_renaming)
                    .replace("{NAME}", first.name)
                    .replace("{NEW_NAME}", newName)
            )
            first.runInIOThreadSafe {
                val result = first.rename(newName = newName)
                if (!result.successful) return@runInIOThreadSafe first.topToastState.showErrorToast(
                    text = result.message ?: R.string.warning_error
                )
                result.newFile?.let {
                    first.mapsViewModel.viewMapDetails(it)
                }
                first.topToastState.showToast(
                    text = context.getString(result.message ?: R.string.maps_renamed)
                        .replace("{NAME}", newName),
                    icon = Icons.Rounded.Edit
                )
            }
            first.mapsViewModel.activeProgress = null
            first.mapsViewModel.loadMaps(context)
        }
    },

    DUPLICATE(
        shortLabel = R.string.maps_duplicate,
        icon = Icons.Rounded.FileCopy,
        availableForMultiSelection = false,
        availableFor = RENAME.availableFor
    ) {
        override suspend fun execute(context: Context, vararg maps: MapFile, args: List<Any>) {
            val first = maps.first()
            val newName = resolveMapNameFromArgs(args, first.name)
            first.mapsViewModel.activeProgress = Progress(
                description = context.getString(R.string.maps_duplicating)
                    .replace("{NAME}", first.name)
                    .replace("{NEW_NAME}", newName)
            )
            first.runInIOThreadSafe {
                val result = first.duplicate(context, newName = newName)
                if (!result.successful) return@runInIOThreadSafe first.topToastState.showErrorToast(
                    text = result.message ?: R.string.warning_error
                )
                result.newFile?.let {
                    first.mapsViewModel.viewMapDetails(it)
                }
                first.topToastState.showToast(
                    text = context.getString(result.message ?: R.string.maps_duplicated)
                        .replace("{NAME}", newName),
                    icon = Icons.Rounded.FileCopy
                )
            }
            first.mapsViewModel.activeProgress = null
            first.mapsViewModel.loadMaps(context)
        }
    },

    IMPORT(
        shortLabel = R.string.maps_import_short,
        longLabel = R.string.maps_import,
        icon = Icons.Rounded.Download,
        availableFor = {
            it.isZip && it.importedState != MapImportedState.IMPORTED
        }
    ) {
        override suspend fun execute(context: Context, vararg maps: MapFile, args: List<Any>) {
            runIOAction(
                *maps,
                context = context,
                singleSuccessMessageId = R.string.maps_imported_single,
                multipleSuccessMessageId = R.string.maps_imported_multiple,
                singleProcessingMessageId = R.string.maps_importing_single,
                multipleProcessingMessageId = R.string.maps_importing_multiple,
                successIcon = icon,
                newName = resolveMapNameFromArgs(args, maps.first().name)
            ) { map ->
                map.import(
                    context,
                    withName = resolveMapNameFromArgs(args, map.name)
                )
            }
        }
    },

    EXPORT(
        shortLabel = R.string.maps_export_short,
        longLabel = R.string.maps_export,
        description = R.string.maps_export_description,
        icon = Icons.Rounded.Upload,
        availableFor = {
            !it.isZip && it.importedState == MapImportedState.IMPORTED
        }
    ) {
        override suspend fun execute(context: Context, vararg maps: MapFile, args: List<Any>) {
            runIOAction(
                *maps,
                context = context,
                singleSuccessMessageId = R.string.maps_exported_single,
                multipleSuccessMessageId = R.string.maps_exported_multiple,
                singleProcessingMessageId = R.string.maps_exporting_single,
                multipleProcessingMessageId = R.string.maps_exporting_multiple,
                successIcon = icon,
                newName = resolveMapNameFromArgs(args, maps.first().name)
            ) { map ->
                map.export(
                    context,
                    withName = resolveMapNameFromArgs(args, map.name)
                )
            }
        }
    },

    EXPORT_CUSTOM_TARGET(
        shortLabel = R.string.maps_exportCustomTarget,
        icon = Icons.AutoMirrored.Filled.AddToHomeScreen,
        availableForMultiSelection = false,
        availableFor = { true }
    ) {
        override suspend fun execute(context: Context, vararg maps: MapFile, args: List<Any>) {
            val first = maps.first()
            val withName = resolveMapNameFromArgs(args, first.name)
            first.mapsViewModel.activeProgress = Progress(
                description = context.getString(R.string.maps_exportCustomTarget_exporting)
                    .replace("{NAME}", first.name)
            )
            first.runInIOThreadSafe {
                val result = first.exportToCustomLocation(context, withName)
                if (result.successful) first.topToastState.showToast(
                    text = context.getString(R.string.maps_exportCustomTarget_exported)
                        .replace("{NAME}", first.name),
                    icon = Icons.AutoMirrored.Filled.AddToHomeScreen
                )
                else first.topToastState.showErrorToast(result.message ?: R.string.warning_error)
                result.newFile?.let { first.mapsViewModel.viewMapDetails(it) }
            }
            first.mapsViewModel.activeProgress = null
        }
    },

    SHARE(
        shortLabel = R.string.maps_share_short,
        longLabel = R.string.maps_share,
        icon = Icons.Rounded.Share,
        availableFor = {
            it.isZip
        }
    ) {
        override suspend fun execute(context: Context, vararg maps: MapFile, args: List<Any>) {
            val first = maps.first()
            val files = maps.map { it.file }
            first.mapsViewModel.activeProgress = Progress(
                description = context.getString(R.string.info_sharing)
            )
            first.runInIOThreadSafe {
                FileUtil.shareFiles(*files.toTypedArray(), context = context)
            }
            first.mapsViewModel.activeProgress = null
        }
    },

    DELETE(
        shortLabel = R.string.maps_delete_short,
        longLabel = R.string.maps_delete,
        icon = Icons.Rounded.Delete,
        destructive = true,
        availableFor = {
            it.importedState != MapImportedState.NONE
        }
    ) {
        override suspend fun execute(context: Context, vararg maps: MapFile, args: List<Any>) {
            maps.first().mapsViewModel.mapsPendingDelete = maps.toList()
        }
    };

    /**
     * Executes the action for [maps].
     */
    abstract suspend fun execute(context: Context, vararg maps: MapFile, args: List<Any> = emptyList())
}

private fun resolveMapNameFromArgs(args: List<Any>, fallback: String): String {
    val str = if (args.isEmpty()) fallback
    else (args[0] as String?)?.ifBlank { fallback } ?: fallback
    return str.replace("\n", "")
}

private suspend fun runIOAction(
    vararg maps: MapFile,
    context: Context,
    singleSuccessMessageId: Int,
    multipleSuccessMessageId: Int,
    singleProcessingMessageId: Int,
    multipleProcessingMessageId: Int,
    successIcon: ImageVector,
    newName: String,
    result: (MapFile) -> MapActionResult
) {
    val first = maps.first()
    val total = maps.size
    val isSingle = total == 1

    var passedProgress = 0
    fun getProgress(): Progress {
        return Progress(
            description = if (isSingle) context.getString(singleProcessingMessageId)
                .replace("{NAME}", first.name)
                .replace("{NEW_NAME}", newName)
            else context.getString(multipleProcessingMessageId)
                .replace("{DONE}", passedProgress.toString())
                .replace("{TOTAL}", total.toString()),
            totalProgress = total.toLong(),
            passedProgress = passedProgress.toLong()
        )
    }

    first.mapsViewModel.activeProgress = getProgress()
    first.runInIOThreadSafe {
        val results = maps.map {
            val executionResult = result(it)
            passedProgress++
            first.mapsViewModel.activeProgress = getProgress()
            it.name to executionResult
        }
        if (isSingle) results.first().let { (mapName, result) ->
            if (result.successful) first.topToastState.showToast(
                text = context.getString(singleSuccessMessageId).replace("{NAME}", mapName),
                icon = successIcon
            ) else first.topToastState.showErrorToast(
                text = context.getString(result.message ?: R.string.warning_error)
            )
            result.newFile?.let { first.mapsViewModel.viewMapDetails(it) }
        } else {
            val successes = results.filter { it.second.successful }
            val fails = results.filter { !it.second.successful }
            if (fails.isEmpty()) first.topToastState.showToast(
                text = context.getString(multipleSuccessMessageId).replace("{COUNT}", successes.size.toString()),
                icon = successIcon
            ) else first.mapsViewModel.showActionFailedDialog(
                successes = successes,
                fails = fails
            )
        }
    }
    first.mapsViewModel.loadMaps(context)
    first.mapsViewModel.activeProgress = null
}