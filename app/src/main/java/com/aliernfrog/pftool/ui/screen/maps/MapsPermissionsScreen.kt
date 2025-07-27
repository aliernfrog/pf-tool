package com.aliernfrog.pftool.ui.screen.maps

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.ui.screen.permissions.PermissionsScreen
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapsPermissionsScreen(
    mapsViewModel: MapsViewModel = koinViewModel(),
    onNavigateSettingsRequest: () -> Unit
) {
    val permissions = remember { arrayOf(
        PermissionData(
            title = R.string.permissions_maps,
            pref = mapsViewModel.prefs.pfMapsDir,
            recommendedPathDescription = R.string.permissions_maps_recommended,
            recommendedPathWarning = R.string.permissions_maps_openPFToCreate,
            useUnrecommendedAnywayDescription = R.string.permissions_maps_useUnrecommendedAnyway,
            content = {
                Text(stringResource(R.string.permissions_maps_description))
            }
        ),
        PermissionData(
            title = R.string.permissions_exportedMaps,
            pref = mapsViewModel.prefs.exportedMapsDir,
            recommendedPathDescription = R.string.permissions_exportedMaps_recommended,
            forceRecommendedPath = false,
            content = {
                Text(stringResource(R.string.permissions_exportedMaps_description))
            }
        )
    ) }

    PermissionsScreen(
        *permissions,
        title = stringResource(R.string.maps),
        onNavigateSettingsRequest = onNavigateSettingsRequest
    ) {
        MapsScreen(
            map = null,
            onNavigateSettingsRequest = onNavigateSettingsRequest,
            onNavigateBackRequest = null
        )
    }
}