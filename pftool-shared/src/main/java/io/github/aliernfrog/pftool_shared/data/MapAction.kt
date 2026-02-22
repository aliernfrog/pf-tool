package io.github.aliernfrog.pftool_shared.data

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.aliernfrog.pftool_shared.impl.IMapActionArguments
import io.github.aliernfrog.pftool_shared.impl.IMapFile

data class MapAction(
    val id: String,
    @StringRes val shortLabel: Int,
    @StringRes val longLabel: Int = shortLabel,
    @StringRes val description: Int? = null,
    val icon: ImageVector,
    val availableForMultiSelection: Boolean,
    val destructive: Boolean = false,
    val availableFor: (map: IMapFile) -> Boolean,
    val execute: suspend (context: Context, maps: List<IMapFile>, args: IMapActionArguments) -> Unit
) {
    companion object {
        const val RENAME_ID = "rename"
        const val DUPLICATE_ID = "duplicate"
    }
}