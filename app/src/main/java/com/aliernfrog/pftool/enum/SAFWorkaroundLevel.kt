package com.aliernfrog.pftool.enum

import androidx.annotation.StringRes
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.viewmodel.PermissionsViewModel
import com.aliernfrog.pftool.util.getKoinInstance

enum class SAFWorkaroundLevel(
    @StringRes val title: Int? = null,
    @StringRes val description: Int? = null,
    val buttons: List<@Composable () -> Unit> = emptyList()
) {
    /**
     * Telling the user to make sure the folder exists.
     */
    MAKE_SURE_FOLDER_EXISTS,

    /**
     * Telling the user to uninstall updates of Files app.
     */
    UNINSTALL_FILES_APP_UPDATES(
        title = R.string.permissions_uninstallFilesAppUpdates,
        description = R.string.permissions_uninstallFilesAppUpdates_description,
        buttons = listOf({
            val permissionsViewModel = getKoinInstance<PermissionsViewModel>()
            Button(
                onClick = {
                    permissionsViewModel.showSAFWorkaroundDialog = false
                    permissionsViewModel.showFilesDowngradeDialog = true
                }
            ) {
                Text(stringResource(R.string.permissions_uninstallFilesAppUpdates_uninstall))
            }
        },{
            val permissionsViewModel = getKoinInstance<PermissionsViewModel>()
            TextButton(
                onClick = {
                    permissionsViewModel.pushSAFWorkaroundLevel()
                }
            ) {
                Text(stringResource(R.string.permissions_uninstallFilesAppUpdates_cant))
            }
        })
    ),

    /**
     * No workarounds anymore.
     */
    SETUP_SHIZUKU(
        title = R.string.permissions_setupShizuku,
        description = R.string.permissions_setupShizuku_description
    )
}