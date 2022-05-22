package com.aliernfrog.pftool.utils

import java.io.File
import java.util.zip.ZipFile

class ZipUtil {
    companion object {
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