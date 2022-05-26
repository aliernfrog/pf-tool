package com.aliernfrog.pftool.utils

import android.util.Log
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class ZipUtil {
    companion object {
        private val allowedMapFiles = arrayOf("colormap.jpg","heightmap.jpg","map.txt","thumbnail.jpg")

        fun zipFolderContent(folderPath: String, zipPath: String) {
            val folder = File(folderPath)
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipPath))).use { zos ->
                folder.walkTopDown().forEach { file ->
                    if (file.absolutePath != folder.absolutePath) {
                        val zipFileName = file.absolutePath.removePrefix(folder.absolutePath).removePrefix("/")
                        Log.d("", "zipFolderContent: ${file.absolutePath} - $zipFileName")
                        val entry = ZipEntry("$zipFileName${(if (file.isDirectory) "/" else "")}")
                        zos.putNextEntry(entry)
                        if (file.isFile) file.inputStream().copyTo(zos)
                    }
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
    }
}