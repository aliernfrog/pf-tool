package com.aliernfrog.pftool.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

class FileUtil {
    companion object {
        fun getFileName(path: String): String {
            val split = path.split("/")
            return split[split.size-1]
        }

        fun shareFile(filePath: String, type: String, context: Context): Intent {
            val file = File(filePath)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            return Intent(Intent.ACTION_SEND).setType(type).putExtra(Intent.EXTRA_STREAM, uri)
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