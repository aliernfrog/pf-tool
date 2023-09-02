package com.aliernfrog.pftool.ui.screen.maps

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.PFMap
import com.aliernfrog.pftool.enum.MapType
import com.aliernfrog.pftool.enum.SortingOptions
import com.aliernfrog.pftool.ui.component.AppScaffold
import com.aliernfrog.pftool.ui.component.ErrorWithIcon
import com.aliernfrog.pftool.ui.component.MapButton
import com.aliernfrog.pftool.ui.component.SegmentedButtons
import com.aliernfrog.pftool.ui.component.form.DividerRow
import com.aliernfrog.pftool.ui.viewmodel.MapsViewModel
import com.aliernfrog.pftool.util.staticutil.UriUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapsListScreen(
    mapsViewModel: MapsViewModel = getViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var sorting by rememberSaveable { mutableStateOf(SortingOptions.ALPHABETICAL) }
    var reverseList by rememberSaveable { mutableStateOf(false) }

    fun pickFile(file: Any) {
        mapsViewModel.chooseMap(file)
        mapsViewModel.mapsListShown = false
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data?.data != null) scope.launch {
            withContext(Dispatchers.IO) {
                val cachedFile = UriUtil.cacheFile(
                    uri = it.data?.data!!,
                    parentName = "maps",
                    context = context
                )
                if (cachedFile != null) pickFile(cachedFile)
                else mapsViewModel.topToastState.showToast(
                    text = R.string.maps_pickMap_failed,
                    icon = Icons.Rounded.PriorityHigh,
                    iconTintColor = TopToastColor.ERROR
                )
            }
        }
    }

    var mapsToShow = when (mapsViewModel.mapsListSelectedSegment) {
        MapType.IMPORTED -> mapsViewModel.importedMaps
        MapType.EXPORTED -> mapsViewModel.exportedMaps
    }.filter {
        it.name.lowercase().contains(searchQuery.lowercase())
    }.sortedWith(when (sorting) {
        SortingOptions.ALPHABETICAL -> compareBy(PFMap::name)
        SortingOptions.DATE -> compareByDescending(PFMap::lastModified)
    })
    if (reverseList) mapsToShow = mapsToShow.reversed()

    val noMapsFoundText = stringResource(when (mapsViewModel.mapsListSelectedSegment) {
        MapType.IMPORTED -> R.string.maps_pickMap_noImportedMaps
        MapType.EXPORTED -> R.string.maps_pickMap_noExportedMaps
    })

    BackHandler(mapsViewModel.mapsListBackButtonShown) {
        mapsViewModel.mapsListShown = false
    }

    AppScaffold(
        title = stringResource(R.string.maps_pickMap),
        topAppBarState = mapsViewModel.listTopAppBarState,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                shape = RoundedCornerShape(16.dp),
                onClick = {
                    val intent = Intent(Intent.ACTION_GET_CONTENT).setType("application/zip")
                    launcher.launch(intent)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.FolderZip,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(stringResource(R.string.maps_pickMap_storage))
            }
        },
        onBackClick = if (!mapsViewModel.mapsListBackButtonShown) null else { {
            mapsViewModel.mapsListShown = false
        } }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {},
                    active = false,
                    onActiveChange = {},
                    leadingIcon = {
                      Icon(
                          imageVector = Icons.Outlined.Search,
                          contentDescription = null
                      )
                    },
                    trailingIcon = {
                        var sortingOptionsShown by rememberSaveable { mutableStateOf(false) }
                        IconButton(
                            onClick = { sortingOptionsShown = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = stringResource(R.string.maps_pickMap_sorting)
                            )
                        }
                        DropdownMenu(
                            expanded = sortingOptionsShown,
                            onDismissRequest = { sortingOptionsShown = false }
                        ) {
                            SortingOptions.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(option.labelId)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = option.iconVector,
                                            contentDescription = null
                                        )
                                    },
                                    trailingIcon = {
                                        RadioButton(
                                            selected = option == sorting,
                                            onClick = { sorting = option }
                                        )
                                    },
                                    onClick = { sorting = option }
                                )
                            }
                            DividerRow(Modifier.padding(vertical = 4.dp))
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.maps_pickMap_reverse)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.SwapVert,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    Checkbox(
                                        checked = reverseList,
                                        onCheckedChange = { reverseList = it }
                                    )
                                },
                                onClick = { reverseList = !reverseList }
                            )
                        }
                    },
                    placeholder = {
                        Text(stringResource(R.string.maps_pickMap_search))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-12).dp)
                        .padding(
                            start = 8.dp,
                            end = 8.dp
                        ),
                    content = {}
                )
                Filter(
                    selectedSegment = mapsViewModel.mapsListSelectedSegment,
                    onSelectedSegmentChange = {
                        mapsViewModel.mapsListSelectedSegment = it
                    }
                )
            }

            item {
                if (mapsToShow.isEmpty()) ErrorWithIcon(
                    error = noMapsFoundText,
                    painter = rememberVectorPainter(Icons.Rounded.LocationOff)
                )
            }

            items(mapsToShow) { map ->
                MapButton(
                    map = map,
                    showMapThumbnail = mapsViewModel.prefs.showMapThumbnailsInList,
                    modifier = Modifier.animateItemPlacement()
                ) {
                    pickFile(map)
                }
            }
        }
    }
}

@Composable
private fun Filter(
    selectedSegment: MapType,
    onSelectedSegmentChange: (MapType) -> Unit
) {
    SegmentedButtons(
        options = listOf(
            stringResource(R.string.maps_pickMap_imported),
            stringResource(R.string.maps_pickMap_exported)
        ),
        selectedIndex = selectedSegment.ordinal,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        onSelectedSegmentChange(MapType.values()[it])
    }
}