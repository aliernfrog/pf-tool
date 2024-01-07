package com.aliernfrog.pftool.impl

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Upload
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.TAG
import com.aliernfrog.pftool.enum.MapImportedState
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import com.aliernfrog.pftool.util.extension.cacheFile
import com.aliernfrog.pftool.util.extension.nameWithoutExtension
import com.aliernfrog.pftool.util.extension.showErrorToast
import com.aliernfrog.pftool.util.extension.showMapAlreadyExistsToast
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
    private val mapsViewModel by inject<MapsViewModel>()
    private val topToastState by inject<TopToastState>()
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
     * Renames the map and also handles the extension.
     */
    suspend fun rename(
        newName: String = mapsViewModel.resolveMapNameInput(),
        showToast: Boolean = true
    ) {
        return runInIOThreadSafe {
            val newFile: Any = when (file) {
                is File -> {
                    val output = File((file.parent?.plus("/") ?: "") + fileName.replace(
                        name, newName
                    ))
                    if (output.exists()) return@runInIOThreadSafe topToastState.showMapAlreadyExistsToast()
                    file.renameTo(output)
                    File(output.absolutePath)
                }
                is DocumentFileCompat -> {
                    val to = fileName.replace(name, newName)
                    if (file.parentFile?.findFile(to)?.exists() == true) return@runInIOThreadSafe topToastState.showMapAlreadyExistsToast()
                    file.renameTo(to)
                    file.parentFile!!.findFile(to)!!
                }
                else -> {}
            }
            mapsViewModel.chooseMap(newFile)
            if (showToast) topToastState.showToast(
                text = contextUtils.getString(R.string.maps_rename_done).replace("{NAME}", newName),
                icon = Icons.Rounded.Edit
            )
        }
    }

    /**
     * Imports the map.
     */
    suspend fun import(
        context: Context,
        withName: String = mapsViewModel.resolveMapNameInput(),
        showToast: Boolean = true
    ) {
        if (importedState == MapImportedState.IMPORTED) return
        runInIOThreadSafe {
            val zipPath = when (file) {
                is File -> file.absolutePath
                is DocumentFileCompat -> file.uri.cacheFile(context)!!.absolutePath
                else -> throw IllegalArgumentException("File class was somehow unknown")
            }
            var output = mapsViewModel.mapsFile.findFile(withName)
            if (output?.exists() == true) return@runInIOThreadSafe topToastState.showMapAlreadyExistsToast()
            output = mapsViewModel.mapsFile.createDirectory(withName)
            ZipUtil.unzipMap(zipPath, output!!, context)
            mapsViewModel.chooseMap(mapsViewModel.mapsFile.findFile(withName))
            if (showToast) topToastState.showToast(
                text = contextUtils.getString(R.string.maps_import_done).replace("{NAME}", withName),
                icon = Icons.Rounded.Download
            )
        }
    }

    /**
     * Exports the map.
     */
    suspend fun export(
        context: Context,
        withName: String = mapsViewModel.resolveMapNameInput(),
        showToast: Boolean = true
    ) {
        if (importedState == MapImportedState.EXPORTED) return
        val zipName = withName.let {
            if (it.endsWith(".zip")) it else "$it.zip"
        }
        runInIOThreadSafe {
            var output = mapsViewModel.exportedMapsFile.findFile(zipName)
            if (output?.exists() == true) return@runInIOThreadSafe topToastState.showMapAlreadyExistsToast()
            output = mapsViewModel.exportedMapsFile.createFile("application/zip", zipName)
            when (file) {
                is File -> ZipUtil.zipMap(file, output!!, context)
                is DocumentFileCompat -> ZipUtil.zipMap(file, output!!, context)
                else -> throw IllegalArgumentException("File class was somehow unknown")
            }
            mapsViewModel.chooseMap(mapsViewModel.exportedMapsFile.findFile(zipName))
            if (showToast) topToastState.showToast(
                text = contextUtils.getString(R.string.maps_export_done).replace("{NAME}", withName),
                icon = Icons.Rounded.Upload
            )
        }
    }

    suspend fun share(context: Context) {
        runInIOThreadSafe {
            FileUtil.shareFile(file, context)
        }
    }

    /**
     * Deletes the map without confirmation.
     */
    suspend fun delete(showToast: Boolean = true) {
        runInIOThreadSafe {
            when (file) {
                is File -> file.delete()
                is DocumentFileCompat -> file.delete()
            }
            mapsViewModel.chooseMap(null)
            if (showToast) topToastState.showToast(
                text = contextUtils.getString(R.string.maps_delete_done).replace("{NAME}", name),
                icon = Icons.Rounded.Delete
            )
        }
    }

    /**
     * Shows delete confirmation dialog for this map.
     */
    fun showDeleteConfirmation() {
        mapsViewModel.pendingMapDelete = this
    }

    private suspend fun runInIOThreadSafe(block: () -> Unit) {
        return withContext(Dispatchers.IO) {
            try {
                block()
            } catch (e: Exception) {
                topToastState.showErrorToast()
                Log.e(TAG, this.toString(), e)
            }
        }
    }
}