package com.aliernfrog.pftool.state

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.provider.DocumentsContract
import androidx.compose.foundation.ScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.aliernfrog.pftool.ConfigKey
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PFMap
import com.aliernfrog.pftool.util.staticutil.FileUtil
import com.aliernfrog.pftool.util.staticutil.ZipUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import com.lazygeniouz.dfc.file.DocumentFileCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
class MapsState(
    _topToastState: TopToastState,
    config: SharedPreferences,
    _pickMapSheetState: ModalBottomSheetState
) {
    private val topToastState = _topToastState
    val scrollState = ScrollState(0)
    val pickMapSheetState = _pickMapSheetState
    val mapsDir = config.getString(ConfigKey.KEY_MAPS_DIR, ConfigKey.DEFAULT_MAPS_DIR)!!
    val mapsExportDir = config.getString(ConfigKey.KEY_MAPS_EXPORT_DIR, ConfigKey.DEFAULT_MAPS_EXPORT_DIR)!!
    private lateinit var mapsFile: DocumentFileCompat
    private val exportedMapsFile = File(mapsExportDir)

    val mapDeleteDialogShown = mutableStateOf(false)
    val importedMaps = mutableStateOf(emptyList<PFMap>())
    val exportedMaps = mutableStateOf(emptyList<PFMap>())
    val chosenMap: MutableState<PFMap?> = mutableStateOf(null)
    val mapNameEdit = mutableStateOf("")
    val lastMapName = mutableStateOf("")

    fun getMap(file: File? = null, documentFile: DocumentFileCompat? = null) {
        if (file != null) {
            val mapName = if (file.isFile) file.nameWithoutExtension else file.name
            if (file.exists()) setChosenMap(PFMap(name = mapName, fileName = file.name, file = file))
            else fileDoesntExist()
        } else if (documentFile != null) {
            val mapName = if (documentFile.isFile()) FileUtil.removeExtension(documentFile.name) else documentFile.name
            if (documentFile.exists()) setChosenMap(PFMap(name = mapName, fileName = documentFile.name, documentFile = documentFile))
            else fileDoesntExist()
        } else {
            setChosenMap(null)
        }
    }

    suspend fun renameChosenMap() {
        val output = mapsFile.findFile(getMapNameEdit())
        if (output != null && output.exists()) fileAlreadyExists()
        else withContext(Dispatchers.IO) {
            mapsFile.findFile(chosenMap.value!!.fileName)?.renameTo(getMapNameEdit())
            getMap(documentFile = mapsFile.findFile(getMapNameEdit()))
            topToastState.showToast(R.string.info_renamedMap, icon = Icons.Rounded.Edit)
            getImportedMaps()
        }
    }

    suspend fun importChosenMap(context: Context) {
        var output = mapsFile.findFile(getMapNameEdit())
        if (output != null && output.exists()) fileAlreadyExists()
        else withContext(Dispatchers.IO) {
            output = mapsFile.createDirectory(getMapNameEdit())
            if (output != null) ZipUtil.unzipMap(chosenMap.value!!.file!!.absolutePath, output!!, context)
            getMap(documentFile = output)
            topToastState.showToast(R.string.info_importedMap, icon = Icons.Rounded.Download)
            getImportedMaps()
        }
    }

    suspend fun exportChosenMap(context: Context) {
        val output = File("${mapsExportDir}/${getMapNameEdit()}.zip")
        if (output.exists()) fileAlreadyExists()
        else withContext(Dispatchers.IO) {
            if (!output.parentFile?.isDirectory!!) output.parentFile?.mkdirs()
            ZipUtil.zipMap(mapsFile.findFile(chosenMap.value!!.fileName)!!, output.absolutePath, context)
            getMap(file = output)
            topToastState.showToast(R.string.info_exportedMap, icon = Icons.Rounded.Upload)
            getExportedMaps()
        }
    }

    suspend fun deleteChosenMap() {
        withContext(Dispatchers.IO) {
            if (chosenMap.value!!.documentFile != null) {
                mapsFile.findFile(chosenMap.value!!.fileName)?.delete()
                getImportedMaps()
            } else {
                chosenMap.value!!.file!!.delete()
                getExportedMaps()
            }
            getMap()
            topToastState.showToast(R.string.info_deletedMap, icon = Icons.Rounded.Delete)
        }
    }

    fun getChosenMapPath(): String? {
        return if (chosenMap.value?.file != null) chosenMap.value!!.file!!.absolutePath
        else if (chosenMap.value?.documentFile != null) "$mapsDir/${chosenMap.value!!.name}"
        else null
    }

    fun getMapNameEdit(): String {
        return mapNameEdit.value.ifBlank { chosenMap.value?.name.toString() }
    }

    private fun setChosenMap(map: PFMap?) {
        chosenMap.value = map
        if (map != null) {
            mapNameEdit.value = map.name
            lastMapName.value = map.name
        }
    }

    private fun fileAlreadyExists() {
        topToastState.showToast(R.string.warning_mapAlreadyExists, icon = Icons.Rounded.PriorityHigh, iconTintColor = TopToastColor.ERROR)
    }

    private fun fileDoesntExist() {
        topToastState.showToast(R.string.warning_fileDoesntExist, icon = Icons.Rounded.PriorityHigh, iconTintColor = TopToastColor.ERROR)
    }

    fun getMapsFile(context: Context): DocumentFileCompat {
        if (::mapsFile.isInitialized) return mapsFile
        val treeId = mapsDir.replace("${Environment.getExternalStorageDirectory()}/", "primary:")
        val treeUri = DocumentsContract.buildTreeDocumentUri("com.android.externalstorage.documents", treeId)
        mapsFile = DocumentFileCompat.fromTreeUri(context, treeUri)!!
        return mapsFile
    }

    suspend fun getImportedMaps() {
        withContext(Dispatchers.IO) {
            val files = mapsFile.listFiles().filter { it.isDirectory() }.sortedBy { it.name.lowercase() }
            val maps = files.map { PFMap(it.name, it.name, it.lastModified, null, it, it.findFile("Thumbnail.jpg")?.uri.toString()) }
            importedMaps.value = maps
        }
    }

    suspend fun getExportedMaps() {
        withContext(Dispatchers.IO) {
            val files = exportedMapsFile.listFiles()?.filter { it.isFile && it.name.lowercase().endsWith(".zip") }?.sortedBy { it.name.lowercase() }
            val maps = files?.map { PFMap(it.nameWithoutExtension, it.name, it.lastModified(), it, null) }
            if (maps != null) exportedMaps.value = maps
        }
    }
}