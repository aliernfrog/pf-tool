package com.aliernfrog.pftool.ui.component.settings

import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.SettingsConstant
import com.aliernfrog.pftool.data.PrefEditItem
import com.aliernfrog.pftool.externalStorageRoot
import com.aliernfrog.pftool.ui.component.form.ButtonRow
import com.aliernfrog.pftool.ui.viewmodel.SettingsViewModel
import com.aliernfrog.pftool.util.extension.resolvePath
import com.aliernfrog.pftool.util.extension.takePersistablePermissions
import org.koin.androidx.compose.getViewModel

@Composable
fun FolderConfiguration(
    settingsViewModel: SettingsViewModel = getViewModel()
) {
    val context = LocalContext.current
    val folders = remember { SettingsConstant.folders }
    var activePref: PrefEditItem? = remember { null }
    val openFolderLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree(), onResult = {
        if (it == null) return@rememberLauncherForActivityResult
        val pref = activePref ?: return@rememberLauncherForActivityResult
        it.takePersistablePermissions(context)
        settingsViewModel.prefs.putString(pref.key, it.toString())
        pref.mutableValue.value = it.toString()
    })

    folders.forEach { folder ->
        fun launchOpenFolder(uri: Uri?) {
            activePref = folder
            openFolderLauncher.launch(uri)
        }

        ButtonRow(
            title = stringResource(folder.labelResourceId!!),
            description = getFolderDescription(folder),
            trailingComponent = {
                IconButton(
                    onClick = {
                        val treeId = "primary:"+folder.default.removePrefix("${Environment.getExternalStorageDirectory()}/")
                        val uri = DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", treeId)
                        launchOpenFolder(uri)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = stringResource(R.string.settings_general_folders_reset)
                    )
                }
            }
        ) {
            launchOpenFolder(null)
        }

        LaunchedEffect(Unit) {
            folder.mutableValue.value = settingsViewModel.prefs.getString(folder.key, "")
        }
    }
}

@Composable
private fun getFolderDescription(folder: PrefEditItem): String {
    var text = folder.mutableValue.value
    if (text.isNotEmpty()) try {
        text = Uri.parse(text).resolvePath()?.removePrefix(externalStorageRoot)
            ?: text
    } catch (_: Exception) {}
    return text.ifEmpty { stringResource(R.string.settings_general_folders_notSet) }
}