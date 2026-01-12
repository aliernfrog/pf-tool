package io.github.aliernfrog.pftool_shared.repository

import io.github.aliernfrog.pftool_shared.data.ServiceFile
import io.github.aliernfrog.pftool_shared.impl.ShizukuManager

class ServiceFileRepository(
    private val shizukuManager: ShizukuManager
) {
    val fileService
        get() = shizukuManager.fileService!!

    fun deleteFile(serviceFile: ServiceFile) {
        return fileService.delete(serviceFile.path)
    }

    fun fileExists(serviceFile: ServiceFile): Boolean {
        return fileService.exists(serviceFile.path)
    }

    fun listFiles(serviceFile: ServiceFile): Array<ServiceFile>? {
        return fileService.listFiles(serviceFile.path)
    }

    fun renameFile(serviceFile: ServiceFile, newPath: String) {
        return fileService.renameFile(serviceFile.path, newPath)
    }

    fun mkdirs(serviceFile: ServiceFile) {
        return fileService.mkdirs(serviceFile.path)
    }
}