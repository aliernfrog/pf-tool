package com.aliernfrog.pftool.service

import android.util.Log
import com.aliernfrog.pftool.IFileService
import com.aliernfrog.pftool.TAG
import java.io.File
import kotlin.system.exitProcess

class FileService : IFileService.Stub() {
    override fun destroy() {
        exitProcess(0)
    }

    override fun exit() {
        destroy()
    }

    override fun listFiles(path: String): Array<String> {
        val files = File(path).listFiles() ?: emptyArray()
        Log.d(TAG, "files: ${files.map { it.absolutePath }}")
        return files.map { it.absolutePath }.toTypedArray()
    }
}