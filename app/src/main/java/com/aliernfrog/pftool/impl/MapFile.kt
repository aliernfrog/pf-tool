package com.aliernfrog.pftool.impl

import android.content.Context
import android.util.Log
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.TAG
import com.aliernfrog.pftool.data.MapActionResult
import com.aliernfrog.pftool.enum.MapImportedState
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
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
    val topToastState by inject<TopToastState>()
    private val contextUtils by inject<ContextUtils>()

    /**
     * Name of the map.
     */
    val name: String = when (file) {
        is File -> if (file.isFile) file.nameWithoutExtension else file.name
        is DocumentFileCompat -> if (file.isFile()) file.nameWithoutExtension else file.name
        else -> throw IllegalArgumentException("Unknown class for a map. Supply File or DocumentFileCompat.")
    }

    /**
     * Name of the map file.
     */
    private val fileName: String = when (file) {
        is File -> file.name
        is DocumentFileCompat -> file.name
        else -> ""
    }

    /**
     * Path of the map. Can be a [File] path or uri.
     */
    val path: String = when (file) {
        is File -> file.absolutePath
        is DocumentFileCompat -> file.uri.toString()
        else -> ""
    }

    /**
     * Whether the file is .zip.
     */
    val isZip: Boolean = fileName.endsWith(".zip")

    /**
     * Size of the map file.
     */
    val size: Long = when (file) {
        is File -> file.size
        is DocumentFileCompat -> file.size
        else -> -1
    }

    /**
     * Last modified time of the map.
     */
    val lastModified: Long = when (file) {
        is File -> file.lastModified()
        is DocumentFileCompat -> file.lastModified
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
    val thumbnailModel: String? = if (importedState != MapImportedState.IMPORTED) null else when (file) {
        is File -> if (file.isDirectory) "$path/Thumbnail.jpg" else null
        is DocumentFileCompat -> if (file.isDirectory()) file.findFile("Thumbnail.jpg")?.uri?.toString() else null
        else -> null
    }

    /**
     * Details of the map. Includes size (KB) and modified time.
     */
    val details: String = contextUtils.stringFunction { context ->
        "${size / 1024} KB | ${FileUtil.lastModifiedFromLong(this.lastModified, context)}"
    }

    /**
     * Renames the map, also handles the extension.
     */
    fun rename(
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
            else -> {}
        }
        return MapActionResult(
            successful = true,
            newFile = newFile
        )
    }

    fun duplicate(
        context: Context,
        newName: String = mapsViewModel.resolveMapNameInput()
    ): MapActionResult {
        val toName = fileName.replace(name, newName)
        when (file) {
            is File -> {
                val targetFile = File((file.parent?.plus("/") ?: "") + toName)
                if (targetFile.exists()) return MapActionResult(
                    successful = false,
                    messageId = R.string.maps_alreadyExists
                )
                if (file.isFile) file.inputStream().use { inputStream ->
                    targetFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                else FileUtil.copyDirectory(file, targetFile)
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
            }
        }
        val newFile: Any = when (file) {
            is File -> File((file.parent?.plus("/") ?: "") + toName)
            is DocumentFileCompat -> file.parentFile!!.findFile(toName)!!
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
            else -> throw IllegalArgumentException("File class was somehow unknown")
        }
        var output = mapsViewModel.mapsFile.findFile(withName)
        if (output?.exists() == true) return MapActionResult(
            successful = false,
            messageId = R.string.maps_alreadyExists
        )
        output = mapsViewModel.mapsFile.createDirectory(withName)
        ZipUtil.unzipMap(zipPath, output!!, context)
        return MapActionResult(
            successful = true,
            newFile = mapsViewModel.mapsFile.findFile(withName)
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
        var output = mapsViewModel.exportedMapsFile.findFile(zipName)
        if (output?.exists() == true) return MapActionResult(
            successful = false,
            messageId = R.string.maps_alreadyExists
        )
        output = mapsViewModel.exportedMapsFile.createFile("application/zip", zipName)
        when (file) {
            is File -> ZipUtil.zipMap(file, output!!, context)
            is DocumentFileCompat -> ZipUtil.zipMap(file, output!!, context)
            else -> throw IllegalArgumentException("File class was somehow unknown")
        }
        return MapActionResult(
            successful = true,
            newFile = mapsViewModel.exportedMapsFile.findFile(zipName)
        )
    }

    /**
     * Deletes the map without confirmation.
     */
    fun delete() {
        when (file) {
            is File -> file.delete()
            is DocumentFileCompat -> file.delete()
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