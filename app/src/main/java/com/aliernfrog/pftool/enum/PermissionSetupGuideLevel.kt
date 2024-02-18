package com.aliernfrog.pftool.enum

import androidx.annotation.StringRes
import com.aliernfrog.pftool.R

enum class PermissionSetupGuideLevel(
    @StringRes val title: Int?,
    @StringRes val description: Int?
) {
    /**
     * Telling the user to make sure they have started the game at least once.
     */
    MAKE_SURE_FOLDER_EXISTS(
        title = R.string.permissions_makeSureFolderExists,
        description = R.string.permissions_makeSureFolderExists_description
    ),

    /**
     * Telling the user to uninstall updates of Files app.
     */
    UNINSTALL_FILES_APP_UPDATES(
        title = R.string.permissions_uninstallFilesAppUpdates,
        description = R.string.permissions_uninstallFilesAppUpdates_description
    ),

    /**
     * Guiding user to setup Shizuku.
     */
    SETUP_SHIZUKU(
        title = null,
        description = null
    ),

    /**
     * Telling user to start Shizuku service if it is not running. Also checking for permissions.
     */
    SHIZUKU(
        title = null,
        description = null
    )
}