package com.aliernfrog.pftool.impl

import android.content.Context
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
import com.aliernfrog.pftool.util.extension.showErrorToast
import com.aliernfrog.pftool.util.staticutil.FileUtil
import io.github.aliernfrog.pftool_shared.data.MapAction
import io.github.aliernfrog.pftool_shared.enum.MapActionResult
import io.github.aliernfrog.pftool_shared.enum.MapImportedState
import io.github.aliernfrog.pftool_shared.impl.Progress
import io.github.aliernfrog.shared.util.SharedString

@Suppress("UNCHECKED_CAST")
val mapActions = listOf(
    MapAction(
        id = MapAction.RENAME_ID,
        shortLabel = SharedString.fromResId(R.string.maps_rename),
        icon = Icons.Rounded.Edit,
        availableForMultiSelection = false,
        availableFor = { it.importedState != MapImportedState.NONE },
        execute = { context, maps, args ->
            val first = maps.first() as MapFile
            val newName = args.resolveMapName(fallback = first.name)
            first.vm.activeProgress = Progress(
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
                    first.vm.viewMapDetails(it)
                }
                first.topToastState.showToast(
                    text = context.getString(result.message ?: R.string.maps_renamed)
                        .replace("{NAME}", newName),
                    icon = Icons.Rounded.Edit
                )
            }
            first.vm.activeProgress = null
            first.vm.loadMaps(context)
        }
    ),

    MapAction(
        id = MapAction.DUPLICATE_ID,
        shortLabel = SharedString.fromResId(R.string.maps_duplicate),
        icon = Icons.Rounded.FileCopy,
        availableForMultiSelection = false,
        availableFor = { it.importedState != MapImportedState.NONE },
        execute = { context, maps, args ->
            val first = maps.first() as MapFile
            val newName = args.resolveMapName(fallback = first.name)
            first.vm.activeProgress = Progress(
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
                    first.vm.viewMapDetails(it)
                }
                first.topToastState.showToast(
                    text = context.getString(result.message ?: R.string.maps_duplicated)
                        .replace("{NAME}", newName),
                    icon = Icons.Rounded.FileCopy
                )
            }
            first.vm.activeProgress = null
            first.vm.loadMaps(context)
        }
    ),

    MapAction(
        id = "import",
        shortLabel = SharedString.fromResId(R.string.maps_import_short),
        longLabel = SharedString.fromResId(R.string.maps_import),
        icon = Icons.Rounded.Download,
        availableFor = {
            (it as MapFile).isZip && it.importedState != MapImportedState.IMPORTED
        },
        availableForMultiSelection = true,
        execute = { context, maps, args ->
            runIOAction(
                maps as List<MapFile>,
                context = context,
                singleSuccessMessageId = R.string.maps_imported_single,
                multipleSuccessMessageId = R.string.maps_imported_multiple,
                singleProcessingMessageId = R.string.maps_importing_single,
                multipleProcessingMessageId = R.string.maps_importing_multiple,
                successIcon = Icons.Rounded.Download,
                newName = args.resolveMapName(fallback = maps.first().name)
            ) { map ->
                map.import(
                    context,
                    withName = args.resolveMapName(fallback = map.name)
                )
            }
        }
    ),

    MapAction(
        id = "export",
        shortLabel = SharedString.fromResId(R.string.maps_export_short),
        longLabel = SharedString.fromResId(R.string.maps_export),
        description = SharedString.fromResId(R.string.maps_export_description),
        icon = Icons.Rounded.Upload,
        availableFor = {
            !(it as MapFile).isZip && it.importedState == MapImportedState.IMPORTED
        },
        availableForMultiSelection = true,
        execute = { context, maps, args ->
            runIOAction(
                maps as List<MapFile>,
                context = context,
                singleSuccessMessageId = R.string.maps_exported_single,
                multipleSuccessMessageId = R.string.maps_exported_multiple,
                singleProcessingMessageId = R.string.maps_exporting_single,
                multipleProcessingMessageId = R.string.maps_exporting_multiple,
                successIcon = Icons.Rounded.Upload,
                newName = args.resolveMapName(fallback = maps.first().name)
            ) { map ->
                map.export(
                    context,
                    withName = args.resolveMapName(fallback = map.name)
                )
            }
        }
    ),

    MapAction(
        id = "exportCustomLocation",
        shortLabel = SharedString.fromResId(R.string.maps_exportCustomTarget),
        icon = Icons.AutoMirrored.Filled.AddToHomeScreen,
        availableFor = { true },
        availableForMultiSelection = false,
        execute = { context, maps, args ->
            val first = maps.first() as MapFile
            val withName = args.resolveMapName(fallback = first.name)
            first.vm.activeProgress = Progress(
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
                result.newFile?.let { first.vm.viewMapDetails(it) }
            }
            first.vm.activeProgress = null
        }
    ),

    MapAction(
        id = "share",
        shortLabel = SharedString.fromResId(R.string.maps_share_short),
        longLabel = SharedString.fromResId(R.string.maps_share),
        icon = Icons.Rounded.Share,
        availableFor = { (it as MapFile).isZip },
        availableForMultiSelection = true,
        execute = { context, maps, _ ->
            val first = maps.first() as MapFile
            val files = maps.map { it.file }
            first.vm.activeProgress = Progress(
                description = context.getString(R.string.info_sharing)
            )
            first.runInIOThreadSafe {
                FileUtil.shareFiles(*files.toTypedArray(), context = context)
            }
            first.vm.activeProgress = null
        }
    ),

    MapAction(
        id = "delete",
        shortLabel = SharedString.fromResId(R.string.maps_delete_short),
        longLabel = SharedString.fromResId(R.string.maps_delete),
        icon = Icons.Rounded.Delete,
        destructive = true,
        availableFor = { it.importedState != MapImportedState.NONE },
        availableForMultiSelection = true,
        execute = { _, maps, _ ->
            (maps.first() as MapFile).vm.mapsPendingDelete = maps.toList() as List<MapFile>
        }
    )
)

private suspend fun runIOAction(
    maps: List<MapFile>,
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

    first.vm.activeProgress = getProgress()
    first.runInIOThreadSafe {
        val results = maps.map {
            val executionResult = result(it)
            passedProgress++
            first.vm.activeProgress = getProgress()
            it.name to executionResult
        }
        if (isSingle) results.first().let { (mapName, result) ->
            if (result.successful) first.topToastState.showToast(
                text = context.getString(singleSuccessMessageId).replace("{NAME}", mapName),
                icon = successIcon
            ) else first.topToastState.showErrorToast(
                text = context.getString(result.message ?: R.string.warning_error)
            )
            result.newFile?.let { first.vm.viewMapDetails(it) }
        } else {
            val successes = results.filter { it.second.successful }
            val fails = results.filter { !it.second.successful }
            if (fails.isEmpty()) first.topToastState.showToast(
                text = context.getString(multipleSuccessMessageId).replace("{COUNT}", successes.size.toString()),
                icon = successIcon
            ) else first.vm.showActionFailedDialog(
                successes = successes,
                fails = fails
            )
        }
    }
    first.vm.loadMaps(context)
    first.vm.activeProgress = null
}