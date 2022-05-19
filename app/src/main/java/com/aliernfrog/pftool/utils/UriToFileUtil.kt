package com.aliernfrog.pftool.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.File
import java.lang.Exception

class UriToFileUtil {
    companion object {
        fun getRealFilePath(uri: Uri, context: Context): String? {
            var docId: String? = null
            try { docId = DocumentsContract.getDocumentId(uri) } catch (e: Exception) {}
            if (docId != null && docId.startsWith("msf")) {
                //was selected from download provider
                val fileName = getFileNameFromContentResolver(uri, context)
                val file = File("${Environment.getExternalStorageDirectory()}/Download/$fileName")
                if (file.exists()) {
                    return file.absolutePath
                } else {
                    try {
                        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                        val fd = parcelFileDescriptor?.fd
                        val pid = android.os.Process.myPid()
                        val mediaFile = File("/proc/$pid/fd/$fd")
                        if (mediaFile.exists()) return mediaFile.absolutePath
                    } catch (e: Exception) {
                        return e.toString()
                    }
                }
            } else {
                //local file was selected
                val returnedPath = getRealPathFromUriApi19(uri, context)
                if (returnedPath != null) {
                    val file = File(returnedPath)
                    if (file.exists()) return returnedPath
                }
            }
            return null
        }

        private fun getFileNameFromContentResolver(uri: Uri, context: Context): String? {
            val projection = arrayOf(MediaStore.Files.FileColumns.DISPLAY_NAME)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    val string = cursor.getString(index)
                    cursor.close()
                    return string
                }
            }
            return null
        }

        private fun getRealPathFromUriApi19(uri: Uri, context: Context): String? {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (uri.authority.equals("com.android.externalstorage.documents")) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":")
                    val type = split[0]
                    if (type == "primary") {
                        return if (split.size > 1) {
                            "${Environment.getExternalStorageDirectory()}/${split[1]}"
                        } else {
                            "${Environment.getExternalStorageDirectory()}/"
                        }
                    }
                }
            }
            return null
        }
    }
}