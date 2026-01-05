package com.aliernfrog.pftool.util.staticutil

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.aliernfrog.pftool.R
import io.github.aliernfrog.pftool_shared.impl.FileWrapper
import java.io.File

class FileUtil {
    companion object {
        fun copyDirectory(source: File, target: File) {
            if (!target.isDirectory) target.mkdirs()
            source.listFiles()!!.forEach { file ->
                val targetFile = File("${target.absolutePath}/${file.name}")
                if (file.isDirectory) copyDirectory(file, targetFile)
                else file.inputStream().use { inputStream ->
                    targetFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        }

        fun shareFiles(vararg files: FileWrapper, context: Context, title: String = context.getString(R.string.action_share)) {
            val isSingle = files.size <= 1
            val sharedFileUris = files.map {
                FileProvider.getUriForFile(context, "${context.packageName}.provider", moveToSharedCache(it, context))
            }
            val firstUri = sharedFileUris.first()
            val intent = Intent(
                if (sharedFileUris.size > 1) Intent.ACTION_SEND_MULTIPLE else Intent.ACTION_SEND
            )
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setDataAndType(firstUri, context.contentResolver.getType(firstUri))
            if (isSingle) intent.putExtra(Intent.EXTRA_STREAM, firstUri)
            else intent.putExtra(Intent.EXTRA_STREAM, ArrayList(sharedFileUris))
            context.startActivity(Intent.createChooser(intent, title))
        }

        private fun moveToSharedCache(file: FileWrapper, context: Context): File {
            val fileName = file.name
            val inputStream = file.inputStream(context)
            val targetFile = File("${context.externalCacheDir}/shared/$fileName")
            targetFile.parentFile?.mkdirs()
            if (targetFile.isFile) targetFile.delete()
            val output = targetFile.outputStream()
            inputStream?.copyTo(output)
            inputStream?.close()
            output.close()
            return File(targetFile.absolutePath)
        }
    }
}