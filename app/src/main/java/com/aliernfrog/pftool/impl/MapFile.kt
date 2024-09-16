package com.aliernfrog.pftool.impl

import android.content.Context
import android.util.Log
import com.aliernfrog.pftool.*
import com.aliernfrog.pftool.data.MapActionResult
import com.aliernfrog.pftool.enum.MapImportedState
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import com.aliernfrog.pftool.util.extension.showErrorToast
import com.aliernfrog.pftool.util.manager.ContextUtils
import com.aliernfrog.pftool.util.staticutil.FileUtil
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class MapFile(
    val file: FileWrapper
): KoinComponent {
    val mapsViewModel by inject<MapsViewModel>()
    val topToastState by inject<TopToastState>()
    private val contextUtils by inject<ContextUtils>()

    /**
     * Name of the map.
     */
    val name: String = file.nameWithoutExtension

    /**
     * Name of the map file.
     */
    private val fileName: String = file.name

    /**
     * Name of the map thumbnail file. Does not check if it exists.
     */
    private val thumbnailFileName: String = "thumbnail.jpg"

    private val allowedMapFiles = arrayOf("colormap.jpg","heightmap.jpg","map.txt",thumbnailFileName)

    /**
     * Path of the map. Can be a [File] path or uri.
     */
    val path: String = file.path

    /**
     * Whether the file is .zip.
     */
    val isZip: Boolean = fileName.lowercase().endsWith(".zip")

    /**
     * Size of the map file.
     */
    val size: Long = file.size

    /**
     * Last modified time of the map.
     */
    val lastModified: Long = file.lastModified

    /**
     * [MapImportedState] of the map.
     */
    val importedState: MapImportedState = if (path.startsWith(mapsViewModel.mapsDir)) MapImportedState.IMPORTED
    else if (path.startsWith(mapsViewModel.exportedMapsDir)) MapImportedState.EXPORTED
    else MapImportedState.NONE
    
    /**
     * Thumbnail model of the map.
     */
    val thumbnailModel = if (importedState != MapImportedState.IMPORTED) null
    else file.findFile(thumbnailFileName)?.painterModel

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
        if (file.parentFile?.findFile(toName)?.exists() == true) return MapActionResult(
            successful = false,
            message = R.string.maps_alreadyExists
        )
        val newFile = file.rename(toName)
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
        val outputName = fileName.replace(name, newName)
        if (file.parentFile?.findFile(outputName)?.exists() == true) return MapActionResult(
            successful = false,
            message = R.string.maps_alreadyExists
        )
        val output = file.parentFile!!.let {
            if (file.isFile) it.createFile(outputName) else it.createDirectory(outputName)
        }!!
        file.copyTo(output, context)
        return MapActionResult(
            successful = true,
            newFile = file.parentFile?.findFile(outputName)
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
        var outputFolder = mapsViewModel.mapsFile.findFile(withName)
        if (outputFolder?.exists() == true) return MapActionResult(
            successful = false,
            message = R.string.maps_alreadyExists
        )
        outputFolder = mapsViewModel.mapsFile.createDirectory(withName)!!
        val fileInputStream = file.inputStream(context)
        val zipInputStream = ZipInputStream(fileInputStream)
        var currentEntry: ZipEntry?
        while ((zipInputStream.nextEntry.also { currentEntry = it }) != null) {
            currentEntry?.let { entry ->
                val entryName = FileUtil.getFileName(entry.name)
                if (!allowedMapFiles.contains(entryName.lowercase())) return@let
                val outputFile = outputFolder.createFile(entryName)
                val outputStream = outputFile!!.outputStream(context)!!
                zipInputStream.copyTo(outputStream)
                outputStream.close()
            }
        }
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
        var outputFile = mapsViewModel.exportedMapsFile.findFile(zipName)
        if (outputFile?.exists() == true) return MapActionResult(
            successful = false,
            message = R.string.maps_alreadyExists
        )
        outputFile = mapsViewModel.exportedMapsFile.createFile(zipName)!!
        outputFile.outputStream(context)!!.use { os ->
            ZipOutputStream(os).use { zos ->
                file.listFiles().filter {
                    it.isFile && allowedMapFiles.contains(FileUtil.getFileName(it.name).lowercase())
                }.forEach { file ->
                    val entry = ZipEntry(file.name)
                    zos.putNextEntry(entry)
                    file.inputStream(context)!!.use { it.copyTo(zos) }
                }
            }
        }
        return MapActionResult(
            successful = true,
            newFile = mapsViewModel.exportedMapsFile.findFile(zipName)
        )
    }

    /**
     * Deletes the map without confirmation.
     */
    fun delete() = file.delete()

    /**
     * Returns thumbnail file of the map if exists, null otherwise.
     */
    fun getThumbnailFile() = if (importedState != MapImportedState.IMPORTED) null
    else file.findFile(thumbnailFileName)

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