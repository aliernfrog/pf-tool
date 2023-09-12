package com.aliernfrog.pftool.util.staticutil

import android.content.Context
import com.lazygeniouz.dfc.file.DocumentFileCompat
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class ZipUtil {
    companion object {
        private val allowedMapFiles = arrayOf("colormap.jpg","heightmap.jpg","map.txt","thumbnail.jpg")

        fun zipMap(folder: DocumentFileCompat, zipFile: DocumentFileCompat, context: Context) {
            ZipOutputStream(context.contentResolver.openOutputStream(zipFile.uri)).use { zos ->
                val files = folder.listFiles().filter {
                    it.isFile() && allowedMapFiles.contains(FileUtil.getFileName(it.name).lowercase())
                }
                files.forEach { file ->
                    val entry = ZipEntry(file.name)
                    zos.putNextEntry(entry)
                    context.contentResolver.openInputStream(file.uri)?.use { it.copyTo(zos) }
                }
            }
        }

        fun unzipMap(zipPath: String, destDocumentFile: DocumentFileCompat, context: Context) {
            val zip = ZipFile(zipPath)
            val entries = zip.entries().asSequence().filter { allowedMapFiles.contains(
                FileUtil.getFileName(
                    it.name
                ).lowercase(Locale.ROOT)) }
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