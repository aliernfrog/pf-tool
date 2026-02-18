package com.aliernfrog.pftool.impl

import android.content.Context
import android.util.Log
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.TAG
import com.aliernfrog.pftool.domain.AppState
import com.aliernfrog.pftool.domain.MapsState
import com.aliernfrog.pftool.util.extension.showReportableErrorToast
import com.aliernfrog.toptoast.state.TopToastState
import com.lazygeniouz.dfc.file.DocumentFileCompat
import io.github.aliernfrog.pftool_shared.data.MapActionResult
import io.github.aliernfrog.pftool_shared.enum.MapImportedState
import io.github.aliernfrog.pftool_shared.impl.FileWrapper
import io.github.aliernfrog.pftool_shared.impl.IMapFile
import io.github.aliernfrog.pftool_shared.impl.ProgressState
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.impl.ContextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class MapFile(
    override val file: FileWrapper
): IMapFile, KoinComponent {
    companion object {
        private const val THUMBNAIL_FILE_NAME: String = "thumbnail.jpg"
        private val mapFolderContent = arrayOf(
            "colormap.jpg",
            "heightmap.jpg",
            "map.txt",
            THUMBNAIL_FILE_NAME
        )
    }

    val mapsState by inject<MapsState>()
    val appState by inject<AppState>()
    val progressState by inject<ProgressState>()
    val topToastState by inject<TopToastState>()
    override val contextUtils by inject<ContextUtils>()

    val isZip: Boolean = fileName.lowercase().endsWith(".zip")

    override val importedState = if (path.startsWith(mapsState.mapsDir)) MapImportedState.IMPORTED
    else if (path.startsWith(mapsState.exportedMapsDir)) MapImportedState.EXPORTED
    else MapImportedState.NONE

    private val thumbnailFile = if (importedState != MapImportedState.IMPORTED || isZip) null
    else file.findFile(THUMBNAIL_FILE_NAME)

    override fun rename(
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

    override fun duplicate(
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

    override fun import(
        context: Context,
        withName: String
    ): MapActionResult {
        if (importedState == MapImportedState.IMPORTED) return MapActionResult(successful = false)
        var outputFolder = mapsState.getMapsFile(context)!!.findFile(withName)
        if (outputFolder?.exists() == true) return MapActionResult(
            successful = false,
            message = R.string.maps_alreadyExists
        )
        outputFolder = mapsState.getMapsFile(context)!!.createDirectory(withName)!!
        val fileInputStream = file.inputStream(context)
        val zipInputStream = ZipInputStream(fileInputStream)
        var currentEntry: ZipEntry?
        while ((zipInputStream.nextEntry.also { currentEntry = it }) != null) {
            currentEntry?.let { entry ->
                val entryName = PFToolSharedUtil.getFileName(entry.name)
                if (!mapFolderContent.contains(entryName.lowercase())) return@let
                val outputFile = outputFolder.createFile(entryName)
                val outputStream = outputFile!!.outputStream(context)!!
                zipInputStream.copyTo(outputStream)
                outputStream.close()
            }
        }
        return MapActionResult(
            successful = true,
            newFile = mapsState.getMapsFile(context)!!.findFile(withName)
        )
    }

    override fun export(
        context: Context,
        withName: String
    ): MapActionResult {
        if (importedState == MapImportedState.EXPORTED) return MapActionResult(successful = false)
        val zipName = "$withName.zip"
        var outputFile = mapsState.getExportedMapsFile(context)!!.findFile(zipName)
        if (outputFile?.exists() == true) return MapActionResult(
            successful = false,
            message = R.string.maps_alreadyExists
        )
        outputFile = mapsState.getExportedMapsFile(context)!!.createFile(zipName)!!
        outputFile.outputStream(context)!!.use { os ->
            ZipOutputStream(os).use { zos ->
                file.listFiles().filter {
                    it.isFile && mapFolderContent.contains(PFToolSharedUtil.getFileName(it.name).lowercase())
                }.forEach { file ->
                    val entry = ZipEntry(file.name)
                    zos.putNextEntry(entry)
                    file.inputStream(context)!!.use { it.copyTo(zos) }
                }
            }
        }
        return MapActionResult(
            successful = true,
            newFile = mapsState.getExportedMapsFile(context)!!.findFile(zipName)
        )
    }

    override suspend fun exportToCustomLocation(
        context: Context,
        withName: String
    ): MapActionResult {
        val uri = appState.safZipFileCreator.createFile(suggestedName = withName)
            ?: return MapActionResult(
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
                    it.isFile && mapFolderContent.contains(PFToolSharedUtil.getFileName(it.name).lowercase())
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

    override fun getThumbnailFile() = thumbnailFile

    override suspend fun runInIOThreadSafe(block: suspend () -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                block()
            } catch (e: Exception) {
                topToastState.showReportableErrorToast(e)
                Log.e(TAG, this.toString(), e)
            }
        }
    }
}