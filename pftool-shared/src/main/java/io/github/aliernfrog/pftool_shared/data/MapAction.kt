package io.github.aliernfrog.pftool_shared.data

import android.content.Context
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.aliernfrog.pftool_shared.impl.IMapActionArguments
import io.github.aliernfrog.pftool_shared.impl.IMapFile
import io.github.aliernfrog.shared.util.SharedString

data class MapAction(
    val shortLabel: SharedString,
    val longLabel: SharedString = shortLabel,
    val description: SharedString? = null,
    val icon: ImageVector,
    val availableForMultiSelection: Boolean,
    val destructive: Boolean = false,
    val availableFor: (map: IMapFile) -> Boolean,
    val execute: suspend (context: Context, maps: List<IMapFile>, args: IMapActionArguments) -> Unit
)