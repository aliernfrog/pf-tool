package com.aliernfrog.pftool.impl

import android.content.Context
import android.util.Log
import com.aliernfrog.pftool.*
import com.aliernfrog.pftool.data.MapActionResult
import com.aliernfrog.pftool.di.getKoinInstance
import com.aliernfrog.pftool.enum.MapImportedState
import com.aliernfrog.pftool.ui.viewmodel.MainViewModel
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import com.aliernfrog.pftool.util.extension.showErrorToast
import com.aliernfrog.toptoast.state.TopToastState
import com.lazygeniouz.dfc.file.DocumentFileCompat
import io.github.aliernfrog.pftool_shared.impl.ContextUtils
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
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
    else getThumbnailFile()?.painterModel

    /**
     * Readable size of the map in KB.
     */
    val readableSize: String = "${size / 1024} KB"

    /**
     * Readable last modified information of the map.
     */
    val readableLastModified = contextUtils.stringFunction { context ->
        PFToolSharedUtil.lastModifiedFromLong(this.lastModified, context)
    }

    /**
     * Details of the map. Includes size (KB) and modified time.
     */
    val details: String = "$readableSize | $readableLastModified"

    /**
     * Renames the map.
     */
    fun rename(
        newName: String
    ): MapActionResult {
        val toName = fileName.replaceFirst(name, newName)
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
        newName: String
    ): MapActionResult {
        val outputName = fileName.replaceFirst(name, newName)
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
        withName: String = this.name
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
                val entryName = PFToolSharedUtil.getFileName(entry.name)
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
        withName: String = this.name
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
                    it.isFile && allowedMapFiles.contains(PFToolSharedUtil.getFileName(it.name).lowercase())
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

    suspend fun exportToCustomLocation(
        context: Context,
        withName: String
    ): MapActionResult {
        val uri = getKoinInstance<MainViewModel>().safZipFileCreator.createFile(suggestedName = withName)
        if (uri == null) return MapActionResult(
            successful = false,
            message = R.string.maps_exportCustomTarget_cancelled
        )
        if (this.isZip) file.inputStream(context).use { input ->
            context.contentResolver.openOutputStream(uri)!!.use { output ->
                input?.copyTo(output)
            }
        } else context.contentResolver.openOutputStream(uri)!!.use { os ->
            ZipOutputStream(os).use { zos ->
                file.listFiles().filter {
                    it.isFile && allowedMapFiles.contains(PFToolSharedUtil.getFileName(it.name).lowercase())
                }.forEach { file ->
                    val entry = ZipEntry(file.name)
                    zos.putNextEntry(entry)
                    file.inputStream(context)!!.use { it.copyTo(zos) }
                }
            }
        }
        return MapActionResult(
            successful = true,
            newFile = DocumentFileCompat.fromSingleUri(context, uri)
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

    suspend fun runInIOThreadSafe(block: suspend () -> Unit) {
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