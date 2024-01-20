package com.aliernfrog.pftool.enum

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.ui.graphics.vector.ImageVector
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.MapActionResult
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.impl.Progress
import com.aliernfrog.pftool.util.staticutil.FileUtil

/**
 * Contains map actions.
 */
@Suppress("unused")
enum class MapAction(
    /**
     * Resource ID to use as a short label. This can be used for one or multiple maps.
     */
    val shortLabelId: Int,

    /**
     * Resource ID to use as a long label. This should be used for only one map.
     */
    val longLabelId: Int = shortLabelId,

    /**
     * Resource ID to use as a description. This should be used for only one map.
     */
    val descriptionId: Int? = null,

    /**
     * Icon of the action.
     */
    val icon: ImageVector,

    /**
     * Whether the action is destructive.
     */
    val destructive: Boolean = false,

    /**
     * Whether the action is available for [map].
     */
    val availableFor: (map: MapFile) -> Boolean
) {
    IMPORT(
        shortLabelId = R.string.maps_import_short,
        longLabelId = R.string.maps_import,
        icon = Icons.Rounded.Download,
        availableFor = {
            it.isZip && it.importedState != MapImportedState.IMPORTED
        }
    ) {
        override suspend fun execute(context: Context, vararg maps: MapFile) {
            runIOAction(
                *maps,
                singleSuccessMessageId = R.string.maps_imported_single,
                multipleSuccessMessageId = R.string.maps_imported_multiple,
                singleProcessingMessageId = R.string.maps_importing_single,
                multipleProcessingMessageId = R.string.maps_importing_multiple,
                successIcon = icon,
                result = { it.import(context) },
                context = context
            )
        }
    },

    EXPORT(
        shortLabelId = R.string.maps_export_short,
        longLabelId = R.string.maps_export,
        descriptionId = R.string.maps_export_description,
        icon = Icons.Rounded.Upload,
        availableFor = {
            !it.isZip && it.importedState == MapImportedState.IMPORTED
        }
    ) {
        override suspend fun execute(context: Context, vararg maps: MapFile) {
            runIOAction(
                *maps,
                singleSuccessMessageId = R.string.maps_exported_single,
                multipleSuccessMessageId = R.string.maps_exported_multiple,
                singleProcessingMessageId = R.string.maps_exporting_single,
                multipleProcessingMessageId = R.string.maps_exporting_multiple,
                successIcon = icon,
                result = { it.export(context) },
                context = context
            )
        }
    },

    SHARE(
        shortLabelId = R.string.maps_share_short,
        longLabelId = R.string.maps_share,
        icon = Icons.Rounded.Share,
        availableFor = {
            it.isZip
        }
    ) {
        override suspend fun execute(context: Context, vararg maps: MapFile) {
            val files = maps.map { it.file }
            maps.first().runInIOThreadSafe {
                FileUtil.shareFiles(*files.toTypedArray(), context = context)
            }
        }
    },

    DELETE(
        shortLabelId = R.string.maps_delete_short,
        longLabelId = R.string.maps_delete,
        icon = Icons.Rounded.Delete,
        destructive = true,
        availableFor = {
            it.importedState != MapImportedState.NONE
        }
    ) {
        override suspend fun execute(context: Context, vararg maps: MapFile) {
            maps.first().mapsViewModel.mapsPendingDelete = maps.toList()
        }
    };

    /**
     * Executes the action for [maps].
     */
    abstract suspend fun execute(context: Context, vararg maps: MapFile)
}

private suspend fun runIOAction(
    vararg maps: MapFile,
    singleSuccessMessageId: Int,
    multipleSuccessMessageId: Int,
    singleProcessingMessageId: Int,
    multipleProcessingMessageId: Int,
    successIcon: ImageVector,
    result: (MapFile) -> MapActionResult,
    context: Context
) {
    val first = maps.first()
    val total = maps.size
    val isSingle = total == 1

    var passedProgress = 0
    fun getProgress(): Progress {
        return Progress(
            description = if (isSingle) context.getString(singleProcessingMessageId)
                .replace("{NAME}", first.name)
                .replace("{NEW_NAME}", first.resolveMapNameInput())
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
            it.resolveMapNameInput() to executionResult
        }
        if (isSingle) results.first().let { (mapName, result) ->
            first.topToastState.showToast(
                text = if (result.successful) context.getString(singleSuccessMessageId).replace("{NAME}", mapName)
                else context.getString(result.messageId ?: R.string.warning_error),
                icon = if (result.successful) successIcon else Icons.Rounded.PriorityHigh
            )
            result.newFile?.let { first.mapsViewModel.chooseMap(it) }
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