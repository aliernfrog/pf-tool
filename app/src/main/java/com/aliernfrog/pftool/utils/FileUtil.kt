package com.aliernfrog.pftool.utils

import java.io.File

class FileUtil {
    companion object {
        fun deleteDirectory(directory: File) {
            val files = directory.listFiles()
            files?.forEach { file ->
                if (file.isDirectory) deleteDirectory(file)
                else file.delete()
            }
            directory.delete()
        }
    }
}