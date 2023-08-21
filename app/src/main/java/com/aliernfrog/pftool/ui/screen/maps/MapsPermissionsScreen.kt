package com.aliernfrog.pftool.ui.screen.maps

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.ConfigKey
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PermissionData
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
            recommendedPathDescriptionId = R.string.permissions_maps_recommended,
            doesntExistHintId = R.string.permissions_recommendedFolder_openPFToCreate,
            getUri = { mapsViewModel.mapsDir },
            onUriUpdate = {
                mapsViewModel.prefs.pfMapsDir = it.toString()
            },
            content = {
                Text(stringResource(R.string.permissions_maps_description))
            }
        ),
        PermissionData(
            titleId = R.string.permissions_exportedMaps,
            recommendedPath = ConfigKey.RECOMMENDED_EXPORTED_MAPS_DIR,
            recommendedPathDescriptionId = R.string.permissions_exportedMaps_recommended,
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