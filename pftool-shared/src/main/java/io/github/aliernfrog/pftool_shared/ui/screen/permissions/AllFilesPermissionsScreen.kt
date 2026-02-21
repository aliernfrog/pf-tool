package io.github.aliernfrog.pftool_shared.ui.screen.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aliernfrog.toptoast.enum.TopToastColor
import io.github.aliernfrog.pftool_shared.enum.StorageAccessType
import io.github.aliernfrog.pftool_shared.ui.viewmodel.IPermissionsViewModel
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.pftool_shared.util.getSharedString
import io.github.aliernfrog.pftool_shared.util.sharedStringResource
import io.github.aliernfrog.shared.ui.component.SizedButton
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.ui.component.verticalSegmentedShape

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AllFilesPermissionsScreen(
    vm: IPermissionsViewModel,
    onUpdateStateRequest: () -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) onUpdateStateRequest()
        else vm.topToastState.showToast(
            text = context.getSharedString(PFToolSharedString::permissionsAllFilesDenied),
            icon = Icons.Default.Close,
            iconTintColor = TopToastColor.ERROR
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        PermissionsScreenAction(
            title = sharedStringResource(PFToolSharedString::permissionsAllFilesTitle),
            description = sharedStringResource(PFToolSharedString::permissionsAllFilesDescription),
            icon = Icons.Default.Security,
            button = {
                SizedButton(
                    onClick = {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    },
                    size = ButtonDefaults.MediumContainerHeight
                ) { textStyle, _, _ ->
                    Text(
                        text = sharedStringResource(PFToolSharedString::permissionsAllFilesGrant),
                        style = textStyle
                    )
                }
            }
        )

        ExpressiveSection(
            title = sharedStringResource(PFToolSharedString::permissionsOther)
        ) {
            ExpressiveButtonRow(
                title = sharedStringResource(PFToolSharedString::permissionsAllFilesSAF),
                description = sharedStringResource(PFToolSharedString::permissionsAllFilesSAFDescription),
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .verticalSegmentedShape()
            ) {
                vm.storageAccessType = StorageAccessType.SAF
            }
        }

        Spacer(Modifier.navigationBarsPadding())
    }
}