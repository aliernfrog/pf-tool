package com.aliernfrog.pftool.utils

class FileUtil {
    companion object {
        fun removeExtension(path: String): String {
            val extensionIndex = path.lastIndexOf(".")
            if (extensionIndex == -1) return path
            return path.substring(0, extensionIndex)
        }
    }
}