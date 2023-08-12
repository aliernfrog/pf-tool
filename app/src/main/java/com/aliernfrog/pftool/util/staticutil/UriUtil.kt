package com.aliernfrog.pftool.util.staticutil

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

class UriUtil {
    companion object {
        /**
         * Caches file from [uri] and returns cached [File].
         * @param uri [Uri] to cache.
         * @param parentName Name of the parent folder in cache dir.
         */
        fun cacheFile(
            uri: Uri,
            parentName: String?,
            context: Context)
        : File? {
            return try {
                val fileName = getFileName(uri, context)
                val outputFile = File("${context.cacheDir.absolutePath}${
                    if (parentName != null) "/$parentName" else ""
                }/$fileName")
                outputFile.parentFile?.mkdirs()
                if (outputFile.exists()) outputFile.delete()
                val input = context.contentResolver.openInputStream(uri)
                val output = outputFile.outputStream()
                input?.copyTo(output)
                input?.close()
                output.close()
                outputFile
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        @SuppressLint("Range")
        private fun getFileName(uri: Uri, context: Context): String {
            var fileName: String? = null
            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                if (cursor?.moveToFirst() == true) fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                cursor?.close()
            }
            if (fileName == null) {
                fileName = uri.path
                val cut = fileName!!.lastIndexOf("/")
                if (cut != -1) fileName = fileName.substring(cut + 1)
            }
            return fileName
        }
    }
}