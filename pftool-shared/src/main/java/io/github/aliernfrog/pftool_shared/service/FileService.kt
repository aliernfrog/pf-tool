package io.github.aliernfrog.pftool_shared.service

import android.os.ParcelFileDescriptor
import androidx.annotation.Keep
import io.github.aliernfrog.pftool_shared.IFileService
import io.github.aliernfrog.pftool_shared.data.ServiceFile
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import java.io.File
import kotlin.system.exitProcess

@Keep
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
        else PFToolSharedUtil.copyDirectory(source, output)
    }

    override fun createNewFile(path: String) {
        val file = File(path)
        if (file.parentFile?.exists() == false) file.parentFile?.mkdirs()
        file.createNewFile()
    }

    override fun delete(path: String) {
        File(path).deleteRecursively()
    }

    override fun exists(path: String): Boolean {
        return File(path).exists()
    }

    override fun getFile(path: String): ServiceFile {
        return ServiceFile.fromFile(File(path))
    }

    override fun listFiles(path: String): Array<ServiceFile> {
        val files = File(path).listFiles() ?: emptyArray()
        return files.map {
            ServiceFile.fromFile(it)
        }.toTypedArray()
    }

    override fun mkdirs(path: String) {
        File(path).mkdirs()
    }

    override fun renameFile(oldPath: String, newPath: String) {
        File(oldPath).renameTo(File(newPath))
    }

    override fun getFd(path: String): ParcelFileDescriptor {
        return ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_WRITE)
    }
}