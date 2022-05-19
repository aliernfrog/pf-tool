package com.aliernfrog.pftool.screens

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
import com.aliernfrog.pftool.composables.BaseScaffold
import com.aliernfrog.pftool.composables.ColumnRounded
import com.aliernfrog.pftool.composables.MainButton
import com.aliernfrog.pftool.utils.UriToFileUtil
import java.io.File

private var mapPath = mutableStateOf("")
private var mapNameEdit = mutableStateOf("")

@Composable
fun MapsScreen(navController: NavController) {
    val context = LocalContext.current
    BaseScaffold(title = context.getString(R.string.manageMaps), navController = navController) {
        PickMapFileButtton()
        MapName()
    }
}

@Composable
fun PickMapFileButtton() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val convertedPath = UriToFileUtil.getRealFilePath(it.data?.data!!, context)
        if (convertedPath != null) {
            getMap(convertedPath, context)
        } else {
            Toast.makeText(context, context.getString(R.string.warning_couldntConvertToPath), Toast.LENGTH_SHORT).show()
        }
    }
    MainButton(
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
fun MapName() {
    if (mapPath.value != "") {
        val context = LocalContext.current
        val file = File(mapPath.value)
        ColumnRounded(title = context.getString(R.string.manageMapsMapName)) {
            OutlinedTextField(
                value = mapNameEdit.value,
                placeholder = { Text(file.name) },
                onValueChange = { mapNameEdit.value = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun getMap(path: String, context: Context) {
    val file = File(path)
    if (file.exists()) {
        mapPath.value = file.absolutePath
        mapNameEdit.value = file.name
    } else {
        Toast.makeText(context, context.getString(R.string.warning_fileDoesntExist), Toast.LENGTH_SHORT).show()
    }
}