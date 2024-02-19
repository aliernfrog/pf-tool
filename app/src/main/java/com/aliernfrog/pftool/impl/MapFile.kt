package com.aliernfrog.pftool.impl

import android.content.Context
import android.util.Log
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.TAG
import com.aliernfrog.pftool.data.MapActionResult
import com.aliernfrog.pftool.data.ServiceFile
import com.aliernfrog.pftool.data.delete
import com.aliernfrog.pftool.data.exists
import com.aliernfrog.pftool.data.nameWithoutExtension
import com.aliernfrog.pftool.data.renameTo
import com.aliernfrog.pftool.enum.MapImportedState
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import com.aliernfrog.pftool.ui.viewmodel.ShizukuViewModel
import com.aliernfrog.pftool.util.extension.cacheFile
import com.aliernfrog.pftool.util.extension.nameWithoutExtension
import com.aliernfrog.pftool.util.extension.showErrorToast
import com.aliernfrog.pftool.util.extension.size
import com.aliernfrog.pftool.util.manager.ContextUtils
import com.aliernfrog.pftool.util.staticutil.FileUtil
import com.aliernfrog.pftool.util.staticutil.ZipUtil
import com.aliernfrog.toptoast.state.TopToastState
import com.lazygeniouz.dfc.file.DocumentFileCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class MapFile(
    /**
     * Map file. Can be a [File] or [DocumentFileCompat].
     */
    val file: Any
): KoinComponent {
    val mapsViewModel by inject<MapsViewModel>()
    private val shizukuViewModel by inject<ShizukuViewModel>()
    val topToastState by inject<TopToastState>()
    private val contextUtils by inject<ContextUtils>()

    /**
     * Name of the map.
     */
    val name: String = when (file) {
        is File -> if (file.isFile) file.nameWithoutExtension else file.name
        is DocumentFileCompat -> if (file.isFile()) file.nameWithoutExtension else file.name
        is ServiceFile -> if (file.isFile) file.nameWithoutExtension else file.name
        else -> throw IllegalArgumentException("Unknown class for a map. Supply File or DocumentFileCompat.")
    }

    /**
     * Name of the map file.
     */
    private val fileName: String = when (file) {
        is File -> file.name
        is DocumentFileCompat -> file.name
        is ServiceFile -> file.name
        else -> ""
    }

    /**
     * Path of the map. Can be a [File] path or uri.
     */
    val path: String = when (file) {
        is File -> file.absolutePath
        is DocumentFileCompat -> file.uri.toString()
        is ServiceFile -> file.path
        else -> ""
    }

    /**
     * Whether the file is .zip.
     */
    val isZip: Boolean = fileName.lowercase().endsWith(".zip")

    /**
     * Size of the map file.
     */
    val size: Long = when (file) {
        is File -> file.size
        is DocumentFileCompat -> file.size
        is ServiceFile -> file.size
        else -> -1
    }

    /**
     * Last modified time of the map.
     */
    val lastModified: Long = when (file) {
        is File -> file.lastModified()
        is DocumentFileCompat -> file.lastModified
        is ServiceFile -> file.lastModified
        else -> -1
    }

    /**
     * [MapImportedState] of the map.
     */
    val importedState: MapImportedState = if (path.startsWith(mapsViewModel.mapsDir)) MapImportedState.IMPORTED
    else if (path.startsWith(mapsViewModel.exportedMapsDir)) MapImportedState.EXPORTED
    else MapImportedState.NONE

    /**
     * Thumbnail model of the map.
     */
    val thumbnailModel: Any? = if (importedState != MapImportedState.IMPORTED) null else when (file) {
        is File -> if (file.isDirectory) "$path/Thumbnail.jpg" else null
        is DocumentFileCompat -> if (file.isDirectory()) file.findFile("Thumbnail.jpg")?.uri?.toString() else null
        is ServiceFile -> if (!file.isFile) shizukuViewModel.fileService!!.getByteArray("$path/Thumbnail.jpg") else null
        else -> null
    }

    /**
     * Details of the map. Includes size (KB) and modified time.
     */
    val details: String = contextUtils.stringFunction { context ->
        "${size / 1024} KB | ${FileUtil.lastModifiedFromLong(this.lastModified, context)}"
    }

    /**
     * Renames the map.
     */
    fun rename(
        newName: String = resolveMapNameInput()
    ): MapActionResult {
        val toName = fileName.replace(name, newName)
        val newFile: Any = when (file) {
            is File -> {
                val output = File((file.parent?.plus("/") ?: "") + toName)
                if (output.exists()) return MapActionResult(
                    successful = false,
                    messageId = R.string.maps_alreadyExists
                )
                file.renameTo(output)
                File(output.absolutePath)
            }
            is DocumentFileCompat -> {
                if (file.parentFile?.findFile(toName)?.exists() == true) return MapActionResult(
                    successful = false,
                    messageId = R.string.maps_alreadyExists
                )
                file.renameTo(toName)
                file.parentFile!!.findFile(toName)!!
            }
            is ServiceFile -> {
                val output = shizukuViewModel.fileService!!.getFile((file.parentPath?.plus("/") ?: "") + toName)
                if (output.exists()) return MapActionResult(
                    successful = false,
                    messageId = R.string.maps_alreadyExists
                )
                file.renameTo(output.path)
                shizukuViewModel.fileService!!.getFile(output.path)
            }
            else -> {}
        }
        return MapActionResult(
            successful = true,
            newFile = newFile
        )
    }

    /**
     * Duplicates the map.
     */
    fun duplicate(
        context: Context,
        newName: String = mapsViewModel.resolveMapNameInput()
    ): MapActionResult {
        val toName = fileName.replace(name, newName)
        val newFile: Any = when (file) {
            is File -> {
                val output = File((file.parent?.plus("/") ?: "") + toName)
                if (output.exists()) return MapActionResult(
                    successful = false,
                    messageId = R.string.maps_alreadyExists
                )
                if (file.isFile) file.inputStream().use { inputStream ->
                    output.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                else FileUtil.copyDirectory(file, output)
                File((file.parent?.plus("/") ?: "") + toName)
            }
            is DocumentFileCompat -> {
                if (file.parentFile!!.findFile(toName)?.exists() == true) return MapActionResult(
                    successful = false,
                    messageId = R.string.maps_alreadyExists
                )
                if (file.isFile()) context.contentResolver.openInputStream(file.uri).use { inputStream ->
                    context.contentResolver.openOutputStream(file.parentFile!!.createFile("", toName)!!.uri).use { outputStream ->
                        inputStream!!.copyTo(outputStream!!)
                    }
                }
                else FileUtil.copyDirectory(
                    file, file.parentFile!!.createDirectory(toName)!!, context
                )
                file.parentFile!!.findFile(toName)!!
            }
            is ServiceFile -> {
                val output = shizukuViewModel.fileService!!.getFile((file.parentPath?.plus("/") ?: "") + toName)
                if (output.exists()) return MapActionResult(
                    successful = false,
                    messageId = R.string.maps_alreadyExists
                )
                shizukuViewModel.fileService!!.copy(file.path, output.path)
                shizukuViewModel.fileService!!.getFile(output.path)
            }
            else -> throw IllegalArgumentException("File class was somehow unknown")
        }
        return MapActionResult(
            successful = true,
            newFile = newFile
        )
    }

    /**
     * Imports the map.
     */
    fun import(
        context: Context,
        withName: String = resolveMapNameInput()
    ): MapActionResult {
        if (importedState == MapImportedState.IMPORTED) return MapActionResult(successful = false)
        val zipPath = when (file) {
            is File -> file.absolutePath
            is DocumentFileCompat -> file.uri.cacheFile(context)!!.absolutePath
            is ServiceFile -> file.path
            else -> throw IllegalArgumentException("File class was somehow unknown")
        }
        val newFile: Any = when (val mapsFile = mapsViewModel.mapsFile) {
            is File -> {
                val output = File(mapsFile.absolutePath + "/$withName")
                if (output.exists()) return MapActionResult(
                    successful = false,
                    messageId = R.string.maps_alreadyExists
                )
                ZipUtil.unzipMap(zipPath, output)
                File(mapsFile.absolutePath + "/$withName")
            }
            is DocumentFileCompat -> {
                var output = mapsFile.findFile(withName)
                if (output?.exists() == true) return MapActionResult(
                    successful = false,
                    messageId = R.string.maps_alreadyExists
                )
                output = mapsFile.createDirectory(withName)
                ZipUtil.unzipMap(zipPath, output!!, context)
                mapsFile.findFile(withName)!!
            }
            is ServiceFile -> {
                val output = shizukuViewModel.fileService!!.getFile(mapsFile.path + "/$withName")
                if (output.exists()) return MapActionResult(
                    successful = false,
                    messageId = R.string.maps_alreadyExists
                )
                shizukuViewModel.fileService!!.unzipMap(zipPath, output.path)
                shizukuViewModel.fileService!!.getFile(output.path)
            }
            else -> throw IllegalArgumentException("File class was somehow unknown")
        }
        return MapActionResult(
            successful = true,
            newFile = newFile
        )
    }

    /**
     * Exports the map.
     */
    fun export(
        context: Context,
        withName: String = resolveMapNameInput()
    ): MapActionResult {
        if (importedState == MapImportedState.EXPORTED) return MapActionResult(successful = false)
        val zipName = "$withName.zip"
        val newFile: Any = when (val exportedMapsFile = mapsViewModel.exportedMapsFile) {
            is File -> {
                val output = File(exportedMapsFile.absolutePath+"/$zipName")
                if (output.exists()) return MapActionResult(
                    successful = false,
                    messageId = R.string.maps_alreadyExists
                )
                ZipUtil.zipMap(file as File, output)
                File(output.absolutePath)
            }
            is DocumentFileCompat -> {
                var output = exportedMapsFile.findFile(zipName)
                if (output?.exists() == true) return MapActionResult(
                    successful = false,
                    messageId = R.string.maps_alreadyExists
                )
                output = exportedMapsFile.createFile("application/zip", zipName)!!
                ZipUtil.zipMap(file as DocumentFileCompat, output, context)
                exportedMapsFile.findFile(zipName)!!
            }
            is ServiceFile -> {
                val output = shizukuViewModel.fileService!!.getFile(exportedMapsFile.path+"/$zipName")
                if (output.exists()) return MapActionResult(
                    successful = false,
                    messageId = R.string.maps_alreadyExists
                )
                shizukuViewModel.fileService!!.zipMap(path, output.path)
                shizukuViewModel.fileService!!.getFile(output.path)
            }
            else -> throw IllegalArgumentException("File class was somehow unknown")
        }
        return MapActionResult(
            successful = true,
            newFile = newFile
        )
    }

    /**
     * Deletes the map without confirmation.
     */
    fun delete() {
        when (file) {
            is File -> file.delete()
            is DocumentFileCompat -> file.delete()
            is ServiceFile -> file.delete()
        }
    }

    /**
     * Returns the user-provided map name if this map is chosen.
     */
    fun resolveMapNameInput(): String {
        return if (mapsViewModel.chosenMap?.path == path) mapsViewModel.resolveMapNameInput() else name
    }

    suspend fun runInIOThreadSafe(block: () -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                block()
            } catch (e: Exception) {
                topToastState.showErrorToast()
                Log.e(TAG, this.toString(), e)
            }
        }
    }
}