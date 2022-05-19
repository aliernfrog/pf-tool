package com.aliernfrog.pftool.ui.screen

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.utils.FileUtil
import com.aliernfrog.pftool.utils.UriToFileUtil
import java.io.File

private var mapPath = mutableStateOf("")
private var mapNameEdit = mutableStateOf("")
private var mapNameOriginal = mutableStateOf("")

@Composable
fun MapsScreen(navController: NavController) {
    val context = LocalContext.current
    PFToolBaseScaffold(title = context.getString(R.string.manageMaps), navController = navController) {
        PickMapFileButtton()
        MapName()
    }
}

@Composable
private fun PickMapFileButtton() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data?.data != null) {
            val convertedPath = UriToFileUtil.getRealFilePath(it.data?.data!!, context)
            if (convertedPath != null) {
                getMap(convertedPath, context)
            } else {
                Toast.makeText(context, context.getString(R.string.warning_couldntConvertToPath), Toast.LENGTH_SHORT).show()
            }
        }
    }
    PFToolButton(
        title = context.getString(R.string.manageMapsPickMap),
        painter = painterResource(id = R.drawable.map),
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
    ) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("application/zip").putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        launcher.launch(intent)
    }
}

@Composable
private fun MapName() {
    if (mapPath.value != "") {
        val context = LocalContext.current
        PFToolColumnRounded(title = context.getString(R.string.manageMapsMapName)) {
            OutlinedTextField(
                value = mapNameEdit.value,
                placeholder = { Text(mapNameOriginal.value) },
                onValueChange = { mapNameEdit.value = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun getMap(path: String, context: Context) {
    val file = File(path)
    if (file.exists()) {
        mapPath.value = file.absolutePath
        mapNameEdit.value = FileUtil.removeExtension(file.name)
        mapNameOriginal.value = mapNameEdit.value
    } else {
        Toast.makeText(context, context.getString(R.string.warning_fileDoesntExist), Toast.LENGTH_SHORT).show()
    }
}