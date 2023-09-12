package com.aliernfrog.pftool.enum

import com.aliernfrog.pftool.R

enum class MapsListSegment(
    val labelId: Int,
    val noMapsFoundTextId: Int
) {
    IMPORTED(
        labelId = R.string.mapsList_imported,
        noMapsFoundTextId = R.string.mapsList_noImportedMaps
    ),

    EXPORTED(
        labelId = R.string.mapsList_exported,
        noMapsFoundTextId = R.string.mapsList_noExportedMaps
    )
}