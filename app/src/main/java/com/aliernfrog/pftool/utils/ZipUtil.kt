package com.aliernfrog.pftool.utils

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class ZipUtil {
    companion object {
        private val allowedMapFiles = arrayOf("colormap.jpg","heightmap.jpg","map.txt","thumbnail.jpg")

        fun zipMap(folderPath: String, zipPath: String) {
            val folder = File(folderPath)
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipPath))).use { zos ->
                val files = folder.listFiles()?.filter { it.isFile && allowedMapFiles.contains(FileUtil.getFileName(it.name).lowercase(Locale.ROOT)) }
                files?.forEach { file ->
                    val entry = ZipEntry(file.name)
                    zos.putNextEntry(entry)
                    file.inputStream().copyTo(zos)
                }
            }
        }

        fun unzipMap(zipPath: String, destPath: String) {
            val dest = File(destPath)
            if (!dest.isDirectory) dest.mkdirs()
            val zip = ZipFile(zipPath)
            val entries = zip.entries().asSequence().filter { allowedMapFiles.contains(FileUtil.getFileName(it.name).lowercase(Locale.ROOT)) }
            entries.forEach { entry ->
                var entryName = entry.name
                if (entryName.contains("/")) entryName = FileUtil.getFileName(entry.name)
                val outputFile = File("${dest.absolutePath}/${entryName}")
                val input = zip.getInputStream(entry)
                val output = outputFile.outputStream()
                input.copyTo(output)
                input.close()
                output.close()
            }
            zip.close()
        }

        fun unzipMap(zipPath: String, destDocumentFile: DocumentFile, context: Context) {
            if (!destDocumentFile.isDirectory) destDocumentFile.parentFile?.createDirectory(destDocumentFile.name.toString())
            val zip = ZipFile(zipPath)
            val entries = zip.entries().asSequence().filter { allowedMapFiles.contains(FileUtil.getFileName(it.name).lowercase(Locale.ROOT)) }
            entries.forEach { entry ->
                var entryName = entry.name
                if (entryName.contains("/")) entryName = FileUtil.getFileName(entry.name)
                var outputFile = destDocumentFile.findFile(entryName)
                if (outputFile == null) outputFile = destDocumentFile.createFile("", entryName)
                val input = zip.getInputStream(entry)
                val output = context.contentResolver.openOutputStream(outputFile?.uri!!)
                input.copyTo(output!!)
                input.close()
                output.close()
            }
        }
    }
}