package com.aliernfrog.pftool.enum

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.ui.graphics.vector.ImageVector
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.impl.MapFile

enum class MapActions(
    val labelId: Int,
    val descriptionId: Int? = null,
    val icon: ImageVector,
    val destructive: Boolean = false,
    val availableFor: (map: MapFile) -> Boolean
) {
    IMPORT(
        labelId = R.string.maps_import,
        icon = Icons.Rounded.Download,
        availableFor = {
            it.isZip && it.importedState != MapImportedState.IMPORTED
        }
    ) {
        override suspend fun execute(map: MapFile, context: Context) {
            map.import(context)
        }
    },

    EXPORT(
        labelId = R.string.maps_export,
        descriptionId = R.string.maps_export_description,
        icon = Icons.Rounded.Upload,
        availableFor = {
            !it.isZip && it.importedState == MapImportedState.IMPORTED
        }
    ) {
        override suspend fun execute(map: MapFile, context: Context) {
            map.export(context)
        }
    },

    SHARE(
        labelId = R.string.action_share,
        icon = Icons.Rounded.Share,
        availableFor = {
            it.isZip
        }
    ) {
        override suspend fun execute(map: MapFile, context: Context) {
            map.share(context)
        }
    },

    DELETE(
        labelId = R.string.maps_delete,
        icon = Icons.Rounded.Delete,
        destructive = true,
        availableFor = {
            it.importedState != MapImportedState.NONE
        }
    ) {
        override suspend fun execute(map: MapFile, context: Context) {
            map.showDeleteConfirmation()
        }
    };

    abstract suspend fun execute(map: MapFile, context: Context)
}