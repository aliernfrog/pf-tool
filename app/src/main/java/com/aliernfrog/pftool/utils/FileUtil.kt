package com.aliernfrog.pftool.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.provider.DocumentsContract
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

        fun checkUriPermission(path: String, context: Context): Boolean {
            val treeId = path.replace("${Environment.getExternalStorageDirectory()}/", "primary:")
            val treeUri = DocumentsContract.buildTreeDocumentUri("com.android.externalstorage.documents", treeId)
            return context.checkUriPermission(treeUri, android.os.Process.myPid(), android.os.Process.myUid(), Intent.FLAG_GRANT_READ_URI_PERMISSION) == PackageManager.PERMISSION_GRANTED
        }
    }
}