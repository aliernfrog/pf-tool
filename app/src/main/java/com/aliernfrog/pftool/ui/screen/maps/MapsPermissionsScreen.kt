package com.aliernfrog.pftool.ui.screen.maps

import android.os.Build
import android.os.Environment
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.ConfigKey
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.ui.dialog.SimpleAlertDialog
import com.aliernfrog.pftool.ui.screen.PermissionsScreen
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun MapsPermissionsScreen(
    mapsViewModel: MapsViewModel = getViewModel()
) {
    val permissions = remember { arrayOf(
        PermissionData(
            titleId = R.string.permissions_maps,
            recommendedPath = ConfigKey.RECOMMENDED_MAPS_DIR,
            getUri = { mapsViewModel.mapsDir },
            onUriUpdate = {
                mapsViewModel.prefs.pfMapsDir = it.toString()
            },
            introDialog = { shown, onDismissRequest, onConfirm ->
                SimpleAlertDialog(
                    shown = shown,
                    onConfirm = onConfirm,
                    onDismissRequest = onDismissRequest
                ) {
                    Text(stringResource(R.string.permissions_maps_recommended))
                    Card {
                        Text(
                            text = ConfigKey.RECOMMENDED_MAPS_DIR.removePrefix(
                                Environment.getExternalStorageDirectory().toString() + "/"
                            ),
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                    Text(stringResource(R.string.permissions_maps_manuallyCreate))
                    Text(stringResource(
                        // Folder picker on Android 7 or below doesn't support automatically navigating
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) R.string.permissions_maps_a8Hint
                        else R.string.permissions_maps_a7Hint
                    ))
                }
            },
            content = {
                Text(stringResource(R.string.permissions_maps_description))
            }
        ),
        PermissionData(
            titleId = R.string.permissions_exportedMaps,
            recommendedPath = ConfigKey.RECOMMENDED_EXPORTED_MAPS_DIR,
            getUri = { mapsViewModel.exportedMapsDir },
            onUriUpdate = {
                mapsViewModel.prefs.exportedMapsDir = it.toString()
            },
            content = {
                Text(stringResource(R.string.permissions_exportedMaps_description))
            }
        )
    ) }

    PermissionsScreen(*permissions) {
        MapsScreen()
    }
}