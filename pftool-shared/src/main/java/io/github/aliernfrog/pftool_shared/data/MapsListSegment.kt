package io.github.aliernfrog.pftool_shared.data

import io.github.aliernfrog.pftool_shared.impl.IMapFile
import io.github.aliernfrog.pftool_shared.repository.MapRepository
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import kotlin.reflect.KProperty1

data class MapsListSegment(
    val label: KProperty1<PFToolSharedString, Int>,
    val noMapsText: KProperty1<PFToolSharedString, Int>,
    val getMaps: (MapRepository) -> List<IMapFile>
)

fun getDefaultMapsListSegments(
    includeSharedMapsSegment: Boolean
): List<MapsListSegment> {
    return listOf(
        MapsListSegment(
            label = PFToolSharedString::mapsListSegmentImported,
            noMapsText = PFToolSharedString::mapsListSegmentImportedNoMaps,
            getMaps = { it.importedMaps.value }
        ),
        MapsListSegment(
            label = PFToolSharedString::mapsListSegmentExported,
            noMapsText = PFToolSharedString::mapsListSegmentExportedNoMaps,
            getMaps = { it.exportedMaps.value }
        ),
        MapsListSegment(
            label = PFToolSharedString::mapsListSegmentShared,
            noMapsText = PFToolSharedString::mapsListSegmentSharedNoMaps,
            getMaps = { it.sharedMaps.value }
        )
    ).filter {
        includeSharedMapsSegment || it.label != PFToolSharedString::mapsListSegmentShared
    }
}