package io.github.aliernfrog.pftool_shared.repository

import android.content.Context
import androidx.core.net.toUri
import com.lazygeniouz.dfc.file.DocumentFileCompat
import io.github.aliernfrog.pftool_shared.enum.StorageAccessType
import io.github.aliernfrog.pftool_shared.impl.FileWrapper
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import java.io.File

class FileRepository(
    private val storageAccessTypePref: () -> BasePreferenceManager.Preference<Int>,
    private val serviceFileRepository: ServiceFileRepository
) {
    private var lastKnownStorageAccessType = -1
    private val cache = mutableMapOf<String, FileWrapper>()

    fun getFile(path: String, context: Context): FileWrapper? {
        val currentStorageAccessType = storageAccessTypePref().value
        if (lastKnownStorageAccessType != currentStorageAccessType) {
            cache.clear()
            lastKnownStorageAccessType = currentStorageAccessType
        }

        cache[path]?.let { return it }

        val file = when (StorageAccessType.entries[currentStorageAccessType]) {
            StorageAccessType.SAF -> {
                val treeUri = path.toUri()
                DocumentFileCompat.fromTreeUri(context, treeUri)
            }

            StorageAccessType.SHIZUKU -> {
                val file = serviceFileRepository.fileService.getFile(path)
                if (!serviceFileRepository.fileExists(file)) serviceFileRepository.mkdirs(file)
                serviceFileRepository.fileService.getFile(path)
            }

            StorageAccessType.ALL_FILES -> {
                val file = File(path)
                if (!file.isDirectory) file.mkdirs()
                File(path)
            }
        }

        if (file == null) return null
        val fileWrapper = FileWrapper(file)
        cache[path] = fileWrapper
        return fileWrapper
    }
}