package com.aliernfrog.pftool.ui.state

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.provider.DocumentsContract
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.aliernfrog.pftool.ConfigKey
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.MapsListItem
import com.aliernfrog.pftool.data.PfMap
import com.aliernfrog.pftool.util.FileUtil
import com.aliernfrog.pftool.util.ZipUtil
import com.aliernfrog.toptoast.TopToastColorType
import com.aliernfrog.toptoast.TopToastManager
import com.lazygeniouz.filecompat.file.DocumentFileCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
class MapsState(
    toastManager: TopToastManager,
    config: SharedPreferences,
    _pickMapSheetState: ModalBottomSheetState,
    _deleteMapSheetState: ModalBottomSheetState
) {
    private val topToastManager = toastManager
    val pickMapSheetState = _pickMapSheetState
    val deleteMapSheetState = _deleteMapSheetState
    val mapsDir = config.getString(ConfigKey.KEY_MAPS_DIR, ConfigKey.DEFAULT_MAPS_DIR)!!
    val mapsExportDir = config.getString(ConfigKey.KEY_MAPS_EXPORT_DIR, ConfigKey.DEFAULT_MAPS_EXPORT_DIR)!!
    private lateinit var mapsFile: DocumentFileCompat
    private val exportedMapsFile = File(mapsExportDir)

    val importedMaps = mutableStateOf(emptyList<MapsListItem>())
    val exportedMaps = mutableStateOf(emptyList<MapsListItem>())
    val chosenMap: MutableState<PfMap?> = mutableStateOf(null)
    val mapNameEdit = mutableStateOf("")
    val lastMapName = mutableStateOf("")

    fun getMap(file: File? = null, documentFile: DocumentFileCompat? = null, context: Context) {
        if (file != null) {
            val mapName = if (file.isFile) file.nameWithoutExtension else file.name
            if (file.exists()) setChosenMap(PfMap(mapName = mapName, fileName = file.name, filePath = file.absolutePath, isFromUri = false))
            else fileDoesntExist(context)
        } else if (documentFile != null) {
            val mapName = if (documentFile.isFile()) FileUtil.removeExtension(documentFile.name) else documentFile.name
            if (documentFile.exists()) setChosenMap(PfMap(mapName = mapName, fileName = documentFile.name, filePath = "$mapsDir/${documentFile.name}", isFromUri = true))
            else fileDoesntExist(context)
        } else {
            setChosenMap(null)
        }
    }

    suspend fun renameChosenMap(context: Context) {
        val output = mapsFile.findFile(getMapNameEdit())
        if (output != null && output.exists()) fileAlreadyExists(context)
        else withContext(Dispatchers.IO) {
            mapsFile.findFile(chosenMap.value!!.fileName)?.renameTo(getMapNameEdit())
            getMap(documentFile = mapsFile.findFile(getMapNameEdit()), context = context)
            topToastManager.showToast(context.getString(R.string.info_renamedMap), iconDrawableId = R.drawable.edit, iconTintColorType = TopToastColorType.PRIMARY)
            getImportedMaps()
        }
    }

    suspend fun importChosenMap(context: Context) {
        var output = mapsFile.findFile(getMapNameEdit())
        if (output != null && output.exists()) fileAlreadyExists(context)
        else withContext(Dispatchers.IO) {
            output = mapsFile.createDirectory(getMapNameEdit())
            if (output != null) ZipUtil.unzipMap(chosenMap.value!!.filePath, output!!, context)
            getMap(documentFile = output, context = context)
            topToastManager.showToast(context.getString(R.string.info_importedMap), iconDrawableId = R.drawable.download, iconTintColorType = TopToastColorType.PRIMARY)
            getImportedMaps()
        }
    }

    suspend fun exportChosenMap(context: Context) {
        val output = File("${mapsExportDir}/${getMapNameEdit()}.zip")
        if (output.exists()) fileAlreadyExists(context)
        else withContext(Dispatchers.IO) {
            if (!output.parentFile?.isDirectory!!) output.parentFile?.mkdirs()
            ZipUtil.zipMap(mapsFile.findFile(chosenMap.value!!.fileName)!!, output.absolutePath, context)
            getMap(file = output, context = context)
            topToastManager.showToast(context.getString(R.string.info_exportedMap), iconDrawableId = R.drawable.share, iconTintColorType = TopToastColorType.PRIMARY)
            getExportedMaps()
        }
    }

    suspend fun deleteChosenMap(context: Context) {
        withContext(Dispatchers.IO) {
            if (chosenMap.value!!.isFromUri) {
                mapsFile.findFile(chosenMap.value!!.fileName)?.delete()
                getImportedMaps()
            } else {
                File(chosenMap.value!!.filePath).delete()
                getExportedMaps()
            }
            getMap(context = context)
            topToastManager.showToast(context.getString(R.string.info_deletedMap), iconDrawableId = R.drawable.trash, iconTintColorType = TopToastColorType.PRIMARY)
        }
    }

    fun getMapNameEdit(): String {
        return mapNameEdit.value.ifBlank { chosenMap.value!!.mapName }
    }

    private fun setChosenMap(map: PfMap?) {
        chosenMap.value = map
        if (map != null) {
            mapNameEdit.value = map.mapName
            lastMapName.value = map.mapName
        }
    }

    private fun fileAlreadyExists(context: Context) {
        topToastManager.showToast(context.getString(R.string.warning_mapAlreadyExists), iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
    }

    private fun fileDoesntExist(context: Context) {
        topToastManager.showToast(context.getString(R.string.warning_fileDoesntExist), iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
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
            val maps = files.map { MapsListItem(it.name, it.name, it.lastModified, null, it, it.findFile("Thumbnail.jpg")?.uri.toString()) }
            importedMaps.value = maps
        }
    }

    suspend fun getExportedMaps() {
        withContext(Dispatchers.IO) {
            val files = exportedMapsFile.listFiles()?.filter { it.isFile && it.name.lowercase().endsWith(".zip") }?.sortedBy { it.name.lowercase() }
            val maps = files?.map { MapsListItem(it.nameWithoutExtension, it.name, it.lastModified(), it, null) }
            if (maps != null) exportedMaps.value = maps
        }
    }
}