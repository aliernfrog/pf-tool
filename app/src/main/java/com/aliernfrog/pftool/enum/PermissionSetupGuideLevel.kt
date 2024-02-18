package com.aliernfrog.pftool.enum

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R

enum class PermissionSetupGuideLevel(
    @StringRes val title: Int? = null,
    @StringRes val description: Int? = null,
    val buttons: @Composable () -> Unit = {}
) {
    /**
     * Telling the user to make sure they have started the game at least once.
     */
    MAKE_SURE_FOLDER_EXISTS,

    /**
     * Telling the user to uninstall updates of Files app.
     */
    UNINSTALL_FILES_APP_UPDATES(
        title = R.string.permissions_uninstallFilesAppUpdates,
        description = R.string.permissions_uninstallFilesAppUpdates_description,
        buttons = {
            val context = LocalContext.current
            Button(
                onClick = {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.parse("package:com.google.android.documentsui")
                        context.startActivity(this)
                    }
                    Toast.makeText(context, context.getString(R.string.permissions_uninstallFilesAppUpdates_guide), Toast.LENGTH_SHORT).show()
                }
            ) {
                Text(stringResource(R.string.permissions_uninstallFilesAppUpdates_uninstall))
            }
        }
    ),

    /**
     * Guiding user to setup Shizuku.
     */
    SHIZUKU
}