package com.aliernfrog.pftool.enum

import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel

@Suppress("unused")
enum class MapsListSegment(
    val labelId: Int,
    val noMapsTextId: Int,
    val getMaps: (MapsViewModel) -> List<MapFile>
) {
    IMPORTED(
        labelId = R.string.mapsList_imported,
        noMapsTextId = R.string.mapsList_noImportedMaps,
        getMaps = { it.importedMaps }
    ),

    EXPORTED(
        labelId = R.string.mapsList_exported,
        noMapsTextId = R.string.mapsList_noExportedMaps,
        getMaps = { it.exportedMaps }
    ),

    SHARED(
        labelId = R.string.mapsList_shared,
        noMapsTextId = R.string.mapsList_noSharedMaps,
        getMaps = { it.sharedMaps }
    )
}