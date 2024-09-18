package com.aliernfrog.pftool.impl

import android.content.Context
import android.os.ParcelFileDescriptor
import com.aliernfrog.pftool.data.ServiceFile
import com.aliernfrog.pftool.data.delete
import com.aliernfrog.pftool.data.exists
import com.aliernfrog.pftool.data.listFiles
import com.aliernfrog.pftool.data.nameWithoutExtension
import com.aliernfrog.pftool.data.renameTo
import com.aliernfrog.pftool.di.getKoinInstance
import com.aliernfrog.pftool.ui.viewmodel.ShizukuViewModel
import com.aliernfrog.pftool.util.extension.nameWithoutExtension
import com.aliernfrog.pftool.util.extension.size
import com.lazygeniouz.dfc.file.DocumentFileCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@Suppress("IMPLICIT_CAST_TO_ANY")
class FileWrapper(
    val file: Any
) {
    private val shizukuViewModel = getKoinInstance<ShizukuViewModel>()
    private val invalidFileClassException = IllegalArgumentException("FileWrapper: unknown class supplied: ${file.javaClass.name}")

    val name: String = when (file) {
        is File -> file.name
        is DocumentFileCompat -> file.name
        is ServiceFile -> file.name
        else -> throw invalidFileClassException
    }

    val path: String = when (file) {
        is File -> file.absolutePath
        is DocumentFileCompat -> file.uri.toString()
        is ServiceFile -> file.path
        else -> throw invalidFileClassException
    }

    val size: Long = when (file) {
        is File -> file.size
        is DocumentFileCompat -> file.size
        is ServiceFile -> file.size
        else -> throw invalidFileClassException
    }

    val lastModified: Long = when (file) {
        is File -> file.lastModified()
        is DocumentFileCompat -> file.lastModified
        is ServiceFile -> file.lastModified
        else -> throw invalidFileClassException
    }

    val isFile: Boolean = when (file) {
        is File -> file.isFile
        is DocumentFileCompat -> file.isFile()
        is ServiceFile -> file.isFile
        else -> throw invalidFileClassException
    }

    val nameWithoutExtension: String = if (isFile) when (file) {
        is File -> file.nameWithoutExtension
        is DocumentFileCompat -> file.nameWithoutExtension
        is ServiceFile -> file.nameWithoutExtension
        else -> throw invalidFileClassException
    } else name

    val parentFile: FileWrapper?
        get() = when (file) {
            is File -> file.parentFile
            is DocumentFileCompat -> file.parentFile
            is ServiceFile -> shizukuViewModel.fileService?.getFile(file.parentPath)
            else -> throw invalidFileClassException
        }?.let { FileWrapper(it) }

    private var cachedByteArray: ByteArray? = null
    private fun getByteArray(ignoreCache: Boolean = false): ByteArray? {
        if (file !is ServiceFile) return null
        if (!ignoreCache) cachedByteArray?.let { return it }
        val fd = shizukuViewModel.fileService!!.getFd(path)
        val input = ParcelFileDescriptor.AutoCloseInputStream(fd)
        val output = ByteArrayOutputStream()
        input.copyTo(output)
        cachedByteArray = output.toByteArray()
        output.close()
        input.close()
        fd.close()
        return cachedByteArray
    }

    val painterModel: Any?
        get() =  if (isFile) when (file) {
            is File, is DocumentFileCompat -> path
            is ServiceFile -> getByteArray()
            else -> throw invalidFileClassException
        } else null

    fun listFiles(): List<FileWrapper> {
        val list: List<Any> = when (file) {
            is File -> file.listFiles()?.toList()
            is DocumentFileCompat -> file.listFiles()
            is ServiceFile -> file.listFiles()?.toList()
            else -> throw invalidFileClassException
        } ?: emptyList()
        return list.map { FileWrapper(it) }
    }

    fun findFile(name: String): FileWrapper? {
        return when (file) {
            is File -> File(file.absolutePath+"/"+name)
            is DocumentFileCompat -> file.findFile(name)
            is ServiceFile -> shizukuViewModel.fileService!!.getFile(file.path+"/"+name)
            else -> throw invalidFileClassException
        }?.let { FileWrapper(it) }
    }

    fun createFile(name: String): FileWrapper? {
        val filePath = this.path+"/"+name
        return when (file) {
            is File -> File(filePath).let {
                it.createNewFile()
                File(filePath)
            }
            is DocumentFileCompat -> file.createFile("", name)
            is ServiceFile -> {
                shizukuViewModel.fileService!!.createNewFile(filePath)
                shizukuViewModel.fileService!!.getFile(filePath)
            }
            else -> throw invalidFileClassException
        }?.let { FileWrapper(it) }
    }

    fun createDirectory(name: String): FileWrapper? {
        val filePath = this.path+"/"+name
        return when (file) {
            is File -> File(filePath).let {
                it.mkdirs()
                File(filePath)
            }
            is DocumentFileCompat -> file.createDirectory(name)
            is ServiceFile -> {
                shizukuViewModel.fileService!!.mkdirs(filePath)
                shizukuViewModel.fileService!!.getFile(filePath)
            }
            else -> throw invalidFileClassException
        }?.let { FileWrapper(it) }
    }

    fun rename(newName: String): FileWrapper? {
        return when (file) {
            is File -> {
                val newPath = (file.parent?.plus("/") ?: "")+newName
                file.renameTo(File(newPath))
                File(newPath)
            }
            is DocumentFileCompat -> {
                file.renameTo(newName)
                file.parentFile?.findFile(newName)
            }
            is ServiceFile -> {
                val newPath = (file.parentPath?.plus("/") ?: "")+newName
                file.renameTo(newPath)
                shizukuViewModel.fileService!!.getFile(newPath)
            }
            else -> throw invalidFileClassException
        }?.let { FileWrapper(it) }
    }

    fun exists(): Boolean {
        return when (file) {
            is File -> file.exists()
            is DocumentFileCompat -> file.exists()
            is ServiceFile -> file.exists()
            else -> throw invalidFileClassException
        }
    }

    fun inputStream(context: Context): InputStream? {
        return when (file) {
            is File -> file.inputStream()
            is DocumentFileCompat -> context.contentResolver.openInputStream(file.uri)
            is ServiceFile -> getByteArray(ignoreCache = true)!!.inputStream()
            else -> throw invalidFileClassException
        }
    }

    fun outputStream(context: Context): OutputStream? {
        return when (file) {
            is File -> file.outputStream()
            is DocumentFileCompat -> context.contentResolver.openOutputStream(file.uri)
            is ServiceFile -> shizukuViewModel.fileService?.getFd(file.path)?.fileDescriptor?.let {
                FileOutputStream(it)
            }
            else -> throw invalidFileClassException
        }
    }

    fun copyFrom(source: FileWrapper, context: Context) {
        source.inputStream(context)?.use { inputStream ->
            outputStream(context)?.use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    fun copyTo(target: FileWrapper, context: Context) {
        if (this.isFile) target.copyFrom(this, context)
        else this.listFiles().forEach { entry ->
            if (entry.isFile) {
                target.createFile(entry.name)?.let { entry.copyTo(it, context) }
            } else {
                target.createDirectory(entry.name)?.let { entry.copyTo(it, context) }
            }
        }
    }

    fun delete() {
        when (file) {
            is File -> file.deleteRecursively()
            is DocumentFileCompat -> file.delete()
            is ServiceFile -> file.delete()
        }
    }
}