package com.aliernfrog.pftool.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.composables.BaseScaffold
import com.aliernfrog.pftool.composables.MainButton
import com.aliernfrog.pftool.utils.UriToFileUtil

@Composable
fun MapsScreen(navController: NavController) {
    val context = LocalContext.current
    BaseScaffold(title = context.getString(R.string.manageMaps), navController = navController) {
        PickMapFileButtton()
    }
}

@Composable
fun PickMapFileButtton() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val convertedPath = UriToFileUtil.getRealFilePath(it.data?.data!!, context)
        if (convertedPath != null) {
            Toast.makeText(context, convertedPath, Toast.LENGTH_SHORT).show()
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