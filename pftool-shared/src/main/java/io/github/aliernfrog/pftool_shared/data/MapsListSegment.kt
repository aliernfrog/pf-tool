package io.github.aliernfrog.pftool_shared.data

import io.github.aliernfrog.pftool_shared.impl.IMapFile
import io.github.aliernfrog.pftool_shared.repository.MapRepository
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.shared.util.SharedString

data class MapsListSegment(
    val label: SharedString,
    val noMapsText: SharedString,
    val getMaps: (MapRepository) -> List<IMapFile>
)

fun getDefaultMapsListSegments(
    includeSharedMapsSegment: Boolean
): List<MapsListSegment> {
    return listOf(
        MapsListSegment(
            label = PFToolSharedString.MapsListSegmentImported,
            noMapsText = PFToolSharedString.MapsListSegmentImportedNoMaps,
            getMaps = { it.importedMaps.value }
        ),
        MapsListSegment(
            label = PFToolSharedString.MapsListSegmentExported,
            noMapsText = PFToolSharedString.MapsListSegmentExportedNoMaps,
            getMaps = { it.exportedMaps.value }
        ),
        MapsListSegment(
            label = PFToolSharedString.MapsListSegmentShared,
            noMapsText = PFToolSharedString.MapsListSegmentSharedNoMaps,
            getMaps = { it.sharedMaps.value }
        )
    ).filter {
        includeSharedMapsSegment || it.label != PFToolSharedString.MapsListSegmentShared
    }
}