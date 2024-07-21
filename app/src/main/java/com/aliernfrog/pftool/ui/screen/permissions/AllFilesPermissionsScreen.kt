package com.aliernfrog.pftool.ui.screen.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.component.CardWithActions
import com.aliernfrog.pftool.ui.viewmodel.PermissionsViewModel
import com.aliernfrog.toptoast.enum.TopToastColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun AllFilesPermissionsScreen(
    permissionsViewModel: PermissionsViewModel = koinViewModel(),
    onUpdateStateRequest: () -> Unit
) {
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) onUpdateStateRequest()
        else permissionsViewModel.topToastState.showToast(
            text = R.string.permissions_allFiles_denied,
            icon = Icons.Default.Close,
            iconTintColor = TopToastColor.ERROR
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        CardWithActions(
            title = stringResource(R.string.permissions_allFiles_title),
            buttons = {
                Button(
                    onClick = {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                ) {
                    Text(stringResource(R.string.permissions_allFiles_grant))
                }
            }
        ) {
            Text(stringResource(R.string.permissions_allFiles_description))
        }
    }
}