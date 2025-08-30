package com.aliernfrog.pftool.ui.screen.maps

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.ui.dialog.CustomMessageDialog
import com.aliernfrog.pftool.ui.dialog.DeleteConfirmationDialog
import com.aliernfrog.pftool.ui.screen.permissions.PermissionsScreen
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapsScreen(
    map: MapFile?,
    mapsViewModel: MapsViewModel = koinViewModel(),
    onNavigateSettingsRequest: () -> Unit,
    onNavigateBackRequest: (() -> Unit)?
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
        MapsScreenSafePermissions(
            map = map,
            onNavigateSettingsRequest = onNavigateSettingsRequest,
            onNavigateBackRequest = onNavigateBackRequest
        )
    }
}

@Composable
private fun MapsScreenSafePermissions(
    map: MapFile?,
    mapsViewModel: MapsViewModel = koinViewModel(),
    onNavigateSettingsRequest: () -> Unit,
    onNavigateBackRequest: (() -> Unit)?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        mapsViewModel.checkAndUpdateMapsFiles(context)
    }

    if (map != null) MapDetailsScreen(
        map = map,
        onNavigateSettingsRequest = onNavigateSettingsRequest,
        onNavigateBackRequest = onNavigateBackRequest
    ) else MapsListScreen(
        title = stringResource(R.string.maps),
        onBackClick = onNavigateBackRequest,
        onNavigateSettingsRequest = onNavigateSettingsRequest,
        onMapPick = {
            mapsViewModel.viewMapDetails(it)
        }
    )

    mapsViewModel.customDialogTitleAndText?.let { (title, text) ->
        CustomMessageDialog(
            title = title,
            text = text,
            icon = Icons.Default.PriorityHigh,
            onDismissRequest = {
                mapsViewModel.customDialogTitleAndText = null
            }
        )
    }

    mapsViewModel.mapsPendingDelete?.let { maps ->
        DeleteConfirmationDialog(
            name = maps.joinToString(", ") { it.name },
            onDismissRequest = { mapsViewModel.mapsPendingDelete = null },
            onConfirmDelete = { scope.launch {
                mapsViewModel.deletePendingMaps(context)
            } }
        )
    }
}