package com.aliernfrog.pftool.ui.screen.maps

import androidx.compose.animation.AnimatedContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.ConfigKey
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PermissionData
import com.aliernfrog.pftool.ui.dialog.CustomMessageDialog
import com.aliernfrog.pftool.ui.dialog.DeleteConfirmationDialog
import com.aliernfrog.pftool.ui.screen.PermissionsScreen
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapsPermissionsScreen(
    mapsViewModel: MapsViewModel = koinViewModel()
) {
    val permissions = remember { arrayOf(
        PermissionData(
            titleId = R.string.permissions_maps,
            recommendedPath = ConfigKey.RECOMMENDED_MAPS_DIR,
            recommendedPathDescriptionId = R.string.permissions_maps_recommended,
            doesntExistHintId = R.string.permissions_recommendedFolder_openPFToCreate,
            getUri = { mapsViewModel.prefs.pfMapsDir },
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
            getUri = { mapsViewModel.prefs.exportedMapsDir },
            onUriUpdate = {
                mapsViewModel.prefs.exportedMapsDir = it.toString()
            },
            content = {
                Text(stringResource(R.string.permissions_exportedMaps_description))
            }
        )
    ) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    PermissionsScreen(*permissions) {
        AnimatedContent(mapsViewModel.mapListShown) { showMapList ->
            if (showMapList) MapsListScreen(
                onBackClick = if (mapsViewModel.mapListBackButtonShown) {
                    { mapsViewModel.mapListShown = false }
                } else null,
                onMapPick = {
                    mapsViewModel.chooseMap(it)
                    mapsViewModel.mapListShown = false
                }
            )
            else MapsScreen()
        }
    }

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