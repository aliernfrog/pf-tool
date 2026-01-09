package com.aliernfrog.pftool.ui.screen.permissions

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import com.aliernfrog.pftool.ui.component.SettingsButton
import io.github.aliernfrog.pftool_shared.data.PermissionData
import io.github.aliernfrog.pftool_shared.ui.screen.permissions.PermissionsScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PermissionsScreen(
    vararg permissionsData: PermissionData,
    title: String,
    onNavigateSettingsRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    PermissionsScreen(
        permissionsData = permissionsData,
        title = title,
        onRestartAppRequest = {},
        onNavigateStorageSettingsRequest = onNavigateSettingsRequest, // TODO navigate to storage settings page
        settingsButton = {
            SettingsButton {
                onNavigateSettingsRequest()
            }
        },
        content = content
    )
}