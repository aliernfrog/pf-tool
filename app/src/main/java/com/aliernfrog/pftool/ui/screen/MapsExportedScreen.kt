package com.aliernfrog.pftool.ui.screen

import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolBaseScaffold
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import java.io.File
import java.util.*

private lateinit var mapsExportDir: String

@Composable
fun MapsExportedScreen(navController: NavController, config: SharedPreferences) {
    val context = LocalContext.current
    mapsExportDir = config.getString("mapsExportDir", "") ?: ""
    PFToolBaseScaffold(title = context.getString(R.string.exportedMaps), navController = navController) {
        ExportedMapsList()
    }
}

@Composable
private fun ExportedMapsList() {
    val context = LocalContext.current
    val files = File(mapsExportDir).listFiles()?.filter { it.isFile && it.name.lowercase(Locale.getDefault()).endsWith(".zip") }?.sortedBy { it.name.lowercase(Locale.getDefault()) }
    if (files == null || files.isEmpty()) {
        PFToolColumnRounded(color = MaterialTheme.colors.error) {
            Text(text = context.getString(R.string.exportedMapsNoMaps), fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onError)
        }
    } else {
        PFToolColumnRounded(color = MaterialTheme.colors.primary) {
            Text(text = context.getString(R.string.exportedMapsHint), fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onPrimary)
        }
        files.forEach {file ->
            PFToolButton(title = file.nameWithoutExtension, painter = painterResource(id = R.drawable.map)) {
                Toast.makeText(context, file.absolutePath, Toast.LENGTH_SHORT).show()
            }
        }
    }
}