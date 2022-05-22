package com.aliernfrog.pftool.utils

import java.io.File


class FileUtil {
    companion object {
        fun removeExtension(path: String): String {
            val extensionIndex = path.lastIndexOf(".")
            if (extensionIndex == -1) return path
            return path.substring(0, extensionIndex)
        }

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