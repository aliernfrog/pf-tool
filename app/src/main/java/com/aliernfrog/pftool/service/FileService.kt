package com.aliernfrog.pftool.service

import android.os.ParcelFileDescriptor
import com.aliernfrog.pftool.IFileService
import com.aliernfrog.pftool.data.ServiceFile
import com.aliernfrog.pftool.util.getServiceFile
import com.aliernfrog.pftool.util.staticutil.FileUtil
import com.aliernfrog.pftool.util.staticutil.ZipUtil
import java.io.File
import kotlin.system.exitProcess

class FileService : IFileService.Stub() {
    override fun destroy() {
        exitProcess(0)
    }

    override fun exit() {
        destroy()
    }

    override fun copy(sourcePath: String, targetPath: String) {
        val source = File(sourcePath)
        val output = File(targetPath)
        if (source.isFile) source.inputStream().use { inputStream ->
            output.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        else FileUtil.copyDirectory(source, output)
    }

    override fun delete(path: String) {
        File(path).deleteRecursively()
    }
    
    override fun exists(path: String): Boolean {
      return File(path).exists()
    }

    override fun getFile(path: String): ServiceFile {
        return getServiceFile(File(path))
    }

    override fun listFiles(path: String): Array<ServiceFile> {
        val files = File(path).listFiles() ?: emptyArray()
        return files.map {
            getServiceFile(it)
        }.toTypedArray()
    }

    override fun mkdirs(path: String) {
        File(path).mkdirs()
    }

    override fun renameFile(oldPath: String, newPath: String) {
        File(oldPath).renameTo(File(newPath))
    }

    override fun unzipMap(path: String, targetPath: String) {
        ZipUtil.unzipMap(path, File(targetPath))
    }

    override fun zipMap(path: String, targetPath: String) {
        ZipUtil.zipMap(File(path), File(targetPath))
    }

    override fun getFd(path: String): ParcelFileDescriptor {
        return ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_ONLY)
    }
}