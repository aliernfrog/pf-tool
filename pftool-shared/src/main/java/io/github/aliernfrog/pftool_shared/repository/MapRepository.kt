package io.github.aliernfrog.pftool_shared.repository

import android.content.Context
import io.github.aliernfrog.pftool_shared.impl.FileWrapper
import io.github.aliernfrog.pftool_shared.impl.IMapFile
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class MapRepository(
    private val importedMapsFinder: MapFileFinder,
    private val exportedMapsFinder: MapFileFinder,
    private val getFileAsMapFile: (FileWrapper) -> IMapFile,
    private val fileRepository: FileRepository
) {
    private val _importedMaps = MutableStateFlow(listOf<IMapFile>())
    val importedMaps = _importedMaps.asStateFlow()

    private val _exportedMaps = MutableStateFlow(listOf<IMapFile>())
    val exportedMaps = _exportedMaps.asStateFlow()

    private val _sharedMaps = MutableStateFlow(listOf<IMapFile>())
    val sharedMaps = _sharedMaps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    fun getImportedMapsFile(context: Context): FileWrapper? = fileRepository.getFile(
        path = importedMapsFinder.pathPref().value,
        context = context
    )

    fun getExportedMapsFile(context: Context): FileWrapper? = fileRepository.getFile(
        path = exportedMapsFinder.pathPref().value,
        context = context
    )

    suspend fun reloadMaps(context: Context) = withContext(Dispatchers.IO) {
        _isLoading.value = true
        mapOf(
            importedMapsFinder to _importedMaps,
            exportedMapsFinder to _exportedMaps
        ).forEach { (finder, listStateFlow) ->
            fileRepository.getFile(finder.pathPref().value, context)?.let { file ->
                listStateFlow.value = file.listFiles()
                    .filter { finder.isMapFile(it) }
                    .map { getFileAsMapFile(it) }
            }
        }
        _isLoading.value = false
    }

    fun setSharedMaps(maps: List<IMapFile>) {
        _sharedMaps.value = maps
    }
}

data class MapFileFinder(
    val pathPref: () -> BasePreferenceManager.Preference<String>,
    val isMapFile: (FileWrapper) -> Boolean
)