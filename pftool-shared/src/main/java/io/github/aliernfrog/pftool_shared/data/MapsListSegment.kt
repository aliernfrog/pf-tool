package io.github.aliernfrog.pftool_shared.data

import io.github.aliernfrog.pftool_shared.impl.IMapFile
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.shared.util.SharedString

data class MapsListSegment(
    val label: SharedString,
    val noMapsText: SharedString,
    val reloadMaps: suspend () -> Unit,
    val getMaps: () -> List<IMapFile>
)

fun getDefaultMapsListSegments(
    includeSharedMapsSegment: Boolean,
    reloadMaps: suspend () -> Unit,
    getImportedMaps: () -> List<IMapFile>,
    getExportedMaps: () -> List<IMapFile>,
    getSharedMaps: () -> List<IMapFile>,
): List<MapsListSegment> {
    return listOf(
        MapsListSegment(
            label = PFToolSharedString.MapsListSegmentImported,
            noMapsText = PFToolSharedString.MapsListSegmentImportedNoMaps,
            reloadMaps = reloadMaps,
            getMaps = getImportedMaps
        ),
        MapsListSegment(
            label = PFToolSharedString.MapsListSegmentExported,
            noMapsText = PFToolSharedString.MapsListSegmentExportedNoMaps,
            reloadMaps = reloadMaps,
            getMaps = getExportedMaps
        ),
        MapsListSegment(
            label = PFToolSharedString.MapsListSegmentShared,
            noMapsText = PFToolSharedString.MapsListSegmentSharedNoMaps,
            reloadMaps = reloadMaps,
            getMaps = getSharedMaps
        )
    ).filter {
        !includeSharedMapsSegment || it.label != PFToolSharedString.MapsListSegmentShared
    }
}