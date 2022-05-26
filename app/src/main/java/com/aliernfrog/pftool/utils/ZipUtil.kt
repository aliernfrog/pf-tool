package com.aliernfrog.pftool.utils

import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class ZipUtil {
    companion object {
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

        fun unzip(zipPath: String, destPath: String) {
            val dest = File(destPath)
            if (!dest.isDirectory) dest.mkdirs()
            val zip = ZipFile(zipPath)
            zip.entries().asSequence().forEach { entry ->
                val outputFile = File("${dest.absolutePath}/${entry.name}")
                if (entry.isDirectory) {
                    if (!outputFile.isDirectory) outputFile.mkdirs()
                } else {
                    val input = zip.getInputStream(entry)
                    val output = outputFile.outputStream()
                    input.copyTo(output)
                    input.close()
                    output.close()
                }
            }
            zip.close()
        }
    }
}