package io.github.aliernfrog.pftool_shared.ui.screen.maps

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.outlined.SdCard
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.pftool_shared.data.MapAction
import io.github.aliernfrog.pftool_shared.data.MapsListSegment
import io.github.aliernfrog.pftool_shared.enum.ListSorting
import io.github.aliernfrog.pftool_shared.enum.ListStyle
import io.github.aliernfrog.pftool_shared.impl.DefaultMapActionArguments
import io.github.aliernfrog.pftool_shared.impl.FileWrapper
import io.github.aliernfrog.pftool_shared.impl.IMapFile
import io.github.aliernfrog.pftool_shared.ui.component.LazyAdaptiveVerticalGrid
import io.github.aliernfrog.pftool_shared.ui.component.maps.GridMapItem
import io.github.aliernfrog.pftool_shared.ui.component.maps.ListMapItem
import io.github.aliernfrog.pftool_shared.ui.sheet.ListViewOptionsSheet
import io.github.aliernfrog.pftool_shared.ui.viewmodel.IMapsListViewModel
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.pftool_shared.util.getSharedString
import io.github.aliernfrog.pftool_shared.util.manager.base.PFToolBasePreferenceManager
import io.github.aliernfrog.pftool_shared.util.sharedStringResource
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppTopBar
import io.github.aliernfrog.shared.ui.component.ErrorWithIcon
import io.github.aliernfrog.shared.ui.component.FloatingActionButton
import io.github.aliernfrog.shared.ui.component.IconButtonWithTooltip
import io.github.aliernfrog.shared.ui.component.SEGMENTOR_DEFAULT_ROUNDNESS
import io.github.aliernfrog.shared.ui.component.SEGMENTOR_SMALL_ROUNDNESS
import io.github.aliernfrog.shared.ui.component.SingleChoiceConnectedButtonGroup
import io.github.aliernfrog.shared.ui.component.util.AnimatedContentShadowWorkaround
import io.github.aliernfrog.shared.ui.component.util.LazyGridScrollAccessibilityListener
import io.github.aliernfrog.shared.ui.component.util.LazyListScrollAccessibilityListener
import io.github.aliernfrog.shared.ui.component.verticalSegmentedShape
import io.github.aliernfrog.shared.ui.theme.AppFABPadding
import io.github.aliernfrog.shared.util.extension.showErrorToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.collections.filter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MapsListScreen(
    title: String = sharedStringResource(PFToolSharedString::maps),
    fileMimeType: String,
    mapsListSegments: List<MapsListSegment>,
    mapActions: List<MapAction>,
    listViewOptions: PFToolBasePreferenceManager.ListViewOptionsPreference,
    showThumbnailsInList: Boolean,
    showMultiSelectionActions: Boolean = true,
    vm: IMapsListViewModel = koinViewModel(),
    multiSelectFloatingActionButton: @Composable (
        selectedMaps: List<IMapFile>, clearSelection: () -> Unit
    ) -> Unit = { _, _ -> },
    settingsButton: (@Composable () -> Unit)? = null,
    onBackClick: (() -> Unit)?,
    onMapPick: (Any) -> Unit
) {
    val context = LocalContext.current
    val topToastState = koinInject<TopToastState>()
    val scope = rememberCoroutineScope()

    val listViewOptionsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pagerState = rememberPagerState {
        mapsListSegments.size
    }
    val currentlyShownSegment = mapsListSegments.getOrNull(pagerState.currentPage)
    val selectedMaps = vm.selectedMaps
    val sharedMaps = vm.sharedMaps.collectAsStateWithLifecycle().value
    val isLoading = vm.isLoading.collectAsStateWithLifecycle().value
    val isMultiSelecting = selectedMaps.isNotEmpty()
    val listStylePref = listViewOptions.styleGroup.getCurrent()
    val gridMaxLineSpanPref = listViewOptions.gridMaxLineSpanGroup.getCurrent()
    val listStyle = ListStyle.entries[listStylePref.value]
    var multiSelectionDropdownShown by remember { mutableStateOf(false) }
    var areAllShownMapsSelected by remember { mutableStateOf(false) }
    var showFABLabel by remember { mutableStateOf(true) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data?.data != null) scope.launch {
            withContext(Dispatchers.IO) {
                val cachedFile = PFToolSharedUtil.cacheFile(
                    uri = it.data?.data!!,
                    parentName = "maps",
                    context = context
                )
                if (cachedFile != null) onMapPick(FileWrapper(cachedFile))
                else topToastState.showErrorToast(
                    text = context.getSharedString(PFToolSharedString::mapsListPickMapFailed),
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            vm.reloadMaps(context)
        }
    }

    LaunchedEffect(sharedMaps, mapsListSegments.size) {
        if (vm.scrolledToShared || sharedMaps.isEmpty()) return@LaunchedEffect
        val sharedMapsPage = mapsListSegments.indexOfFirst {
            it.label === PFToolSharedString::mapsListSegmentShared
        }
        pagerState.scrollToPage(sharedMapsPage)
        vm.scrolledToShared = true
    }

    if (isMultiSelecting) LaunchedEffect(
        pagerState.currentPage,
        selectedMaps.size
    ) {
        currentlyShownSegment?.let { segment ->
            val maps = vm.getMapsForSegment(segment)
            areAllShownMapsSelected = selectedMaps.containsAll(maps)
        }
    }

    BackHandler(
        enabled = isMultiSelecting || onBackClick != null
    ) {
        if (isMultiSelecting) selectedMaps.clear()
        else onBackClick?.invoke()
    }

    ListViewOptionsSheet(
        sheetState = listViewOptionsSheetState,
        listViewOptionsPreference = listViewOptions
    )

    AppScaffold(
        topBar = { scrollBehavior ->
            AnimatedContent(targetState = isMultiSelecting) { multiSelecting ->
                AppTopBar(
                    title = if (!multiSelecting) title
                    else sharedStringResource(PFToolSharedString::mapsListMultiSelection)
                        .replace("{COUNT}", selectedMaps.size.toString()),
                    scrollBehavior = scrollBehavior,
                    navigationIcon = if (multiSelecting) Icons.Default.Close else Icons.AutoMirrored.Rounded.ArrowBack,
                    onNavigationClick = if (multiSelecting) { {
                        selectedMaps.clear()
                    } } else onBackClick,
                    actions = {
                        if (multiSelecting) {
                            IconButtonWithTooltip(
                                icon = rememberVectorPainter(
                                    if (areAllShownMapsSelected) Icons.Default.Deselect else Icons.Default.SelectAll
                                ),
                                contentDescription = sharedStringResource(
                                    if (areAllShownMapsSelected) PFToolSharedString::actionSelectDeselectAll
                                    else PFToolSharedString::actionSelectSelectAll
                                ),
                                onClick = {
                                    currentlyShownSegment?.let { segment ->
                                        val maps = vm.getMapsForSegment(segment)
                                        if (areAllShownMapsSelected) selectedMaps.removeAll(maps)
                                        else selectedMaps.addAll(
                                            maps.filter { !selectedMaps.contains(it) }
                                        )
                                    }
                                }
                            )
                            if (showMultiSelectionActions) Box {
                                IconButtonWithTooltip(
                                    icon = rememberVectorPainter(Icons.Default.MoreVert),
                                    contentDescription = sharedStringResource(PFToolSharedString::actionMore),
                                    onClick = { multiSelectionDropdownShown = true }
                                )
                                MultiSelectionDropdown(
                                    expanded = multiSelectionDropdownShown,
                                    maps = selectedMaps,
                                    actions = mapActions.filter { action ->
                                        action.availableForMultiSelection && !selectedMaps.any { map ->
                                            !action.availableFor(map)
                                        }
                                    },
                                    onDismissRequest = { clearSelection ->
                                        multiSelectionDropdownShown = false
                                        if (clearSelection) selectedMaps.clear()
                                    }
                                )
                            }
                        } else {
                            Crossfade(isLoading) { showLoading ->
                                if (showLoading) CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp).padding(8.dp)
                                )
                                else IconButtonWithTooltip(
                                    icon = rememberVectorPainter(Icons.Default.Refresh),
                                    contentDescription = sharedStringResource(PFToolSharedString::mapsListReload),
                                    onClick = {
                                        vm.reloadMaps(context)
                                    }
                                )
                            }
                            settingsButton?.invoke()
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            AnimatedContentShadowWorkaround(
                targetState = !isMultiSelecting,
                modifier = Modifier.navigationBarsPadding()
            ) { showStorage ->
                if (showStorage) {
                    FloatingActionButton(
                        icon = Icons.Outlined.SdCard,
                        text = sharedStringResource(PFToolSharedString::mapsListStorage),
                        expanded = showFABLabel,
                        onClick = {
                            val intent =
                                Intent(Intent.ACTION_GET_CONTENT).setType(fileMimeType)
                            launcher.launch(intent)
                        }
                    )
                } else multiSelectFloatingActionButton(selectedMaps) {
                    selectedMaps.clear()
                }
            }
        }
    ) {
        @Composable
        fun SegmentSummary(
            segment: MapsListSegment,
            shownMaps: List<IMapFile>,
            modifier: Modifier = Modifier
        ) {
            SegmentSummary(
                isLoadingMaps = isLoading,
                isSearching = searchQuery.isNotEmpty(),
                currentSegment = segment,
                shownMapCount = shownMaps.size,
                modifier = modifier
            )
        }

        @Composable
        fun MapItem(map: IMapFile, isGrid: Boolean, modifier: Modifier = Modifier) {
            val selected = if (isMultiSelecting) selectedMaps.any {
                it.path == map.path
            } else null

            fun toggleSelection() {
                selectedMaps.run {
                    if (selected == true) remove(map) else add(map)
                }
            }

            val scale by animateFloatAsState(
                if (selected == true) 0.95f else 1f
            )
            val roundness by animateDpAsState(
                if (selected == true) SEGMENTOR_DEFAULT_ROUNDNESS else SEGMENTOR_SMALL_ROUNDNESS
            )

            if (isGrid) GridMapItem(
                map = map,
                selected = selected,
                showMapThumbnail = showThumbnailsInList,
                onSelectedChange = { toggleSelection() },
                onLongClick = { toggleSelection() },
                aspectRatio = 1f,
                modifier = modifier
                    .scale(scale)
                    .clip(RoundedCornerShape(roundness))
            ) {
                if (isMultiSelecting) toggleSelection()
                else onMapPick(map)
            }
            else ListMapItem(
                map = map,
                selected = selected,
                showMapThumbnail = showThumbnailsInList,
                onSelectedChange = { toggleSelection() },
                onLongClick = { toggleSelection() },
                modifier = modifier.scale(scale).clip(RoundedCornerShape(roundness))
            ) {
                if (isMultiSelecting) toggleSelection()
                else onMapPick(map)
            }
        }

        BoxWithConstraints {
            val viewportHeight = maxHeight
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Search(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    onShowListViewOptionsRequest = { scope.launch {
                        listViewOptionsSheetState.show()
                    } }
                )

                SingleChoiceConnectedButtonGroup(
                    choices = mapsListSegments.map {
                        sharedStringResource(it.label)
                    },
                    selectedIndex = mapsListSegments.indexOfFirst { it == currentlyShownSegment },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    scope.launch {
                        pagerState.animateScrollToPage(it, animationSpec = tween(300))
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    beyondViewportPageCount = 1,
                    modifier = Modifier
                        .heightIn(max = viewportHeight)
                        .nestedScroll(remember {
                            object : NestedScrollConnection {
                                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                                    return if (available.y > 0) Offset.Zero else Offset(
                                        x = 0f,
                                        y = -scrollState.dispatchRawDelta(-available.y)
                                    )
                                }
                            }
                        })
                ) { page ->
                    val segment = mapsListSegments[page]
                    val mapsToShow = vm.getMapsForSegment(segment)
                        .filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        }
                        .sortedWith { m1, m2 ->
                            ListSorting.entries[listViewOptions.sorting.value].comparator.compare(m1.file, m2.file)
                        }
                        .let {
                            if (listViewOptions.sortingReversed.value) it.reversed() else it
                        }

                    val lazyListState = rememberLazyListState()
                    val lazyGridState = rememberLazyGridState()

                    LazyListScrollAccessibilityListener(
                        lazyListState = lazyListState,
                        onShowLabelsStateChange = { showFABLabel = it }
                    )

                    LazyGridScrollAccessibilityListener(
                        lazyGridState = lazyGridState,
                        onShowLabelsStateChange = { showFABLabel = it }
                    )

                    AnimatedContent(targetState = listStyle) { style ->
                        when (style) {
                            ListStyle.LIST -> LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = lazyListState
                            ) {
                                item {
                                    SegmentSummary(
                                        segment = segment,
                                        shownMaps = mapsToShow,
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )
                                }

                                itemsIndexed(mapsToShow) { index, map ->
                                    MapItem(
                                        map = map,
                                        isGrid = false,
                                        modifier = Modifier
                                            .padding(horizontal = 12.dp)
                                            .verticalSegmentedShape(
                                                index = index,
                                                totalSize = mapsToShow.size,
                                                spacing = 4.dp,
                                                containerColor = Color.Transparent
                                            )
                                    )
                                }

                                item {
                                    Footer()
                                }
                            }

                            ListStyle.GRID -> LazyAdaptiveVerticalGrid(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp),
                                state = lazyGridState,
                                maxLineSpan = gridMaxLineSpanPref.value
                            ) { maxLineSpan: Int ->
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    SegmentSummary(
                                        segment = segment,
                                        shownMaps = mapsToShow,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    )
                                }

                                items(mapsToShow) { map ->
                                    MapItem(
                                        map = map,
                                        isGrid = true,
                                        modifier = Modifier.padding(2.dp)
                                    )
                                }

                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Footer()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SegmentSummary(
    isLoadingMaps: Boolean,
    isSearching: Boolean,
    currentSegment: MapsListSegment,
    shownMapCount: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        if (shownMapCount == 0) {
            if (isLoadingMaps) Box(Modifier.fillMaxSize()) {
                ContainedLoadingIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = 24.dp)
                )
            }
            else AnimatedContent(isSearching) { searching ->
                ErrorWithIcon(
                    description = sharedStringResource(
                        if (searching) PFToolSharedString::mapsListSearchNoMatches else currentSegment.noMapsText
                    ),
                    icon = rememberVectorPainter(
                        if (searching) Icons.Rounded.SearchOff else Icons.Rounded.LocationOff
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else Text(
            text = sharedStringResource(PFToolSharedString::mapsListCount)
                .replace("{COUNT}", shownMapCount.toString()),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .padding(bottom = 4.dp)
        )
    }
}

@Composable
private fun Footer() {
    Spacer(Modifier.navigationBarsPadding().height(AppFABPadding))
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Search(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onShowListViewOptionsRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = {},
                expanded = false,
                onExpandedChange = {},
                leadingIcon = {
                    if (searchQuery.isNotEmpty()) IconButtonWithTooltip(
                        icon = rememberVectorPainter(Icons.Default.Clear),
                        contentDescription = sharedStringResource(PFToolSharedString::mapsListSearchClear),
                        onClick = { onSearchQueryChange("") }
                    )
                    else Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButtonWithTooltip(
                        icon = rememberVectorPainter(Icons.AutoMirrored.Filled.Sort),
                        contentDescription = sharedStringResource(PFToolSharedString::listOptions),
                        onClick = onShowListViewOptionsRequest
                    )
                },
                placeholder = {
                    Text(sharedStringResource(PFToolSharedString::mapsListSearch))
                }
            )
        },
        expanded = false,
        onExpandedChange = {},
        content = {},
        modifier = modifier
            .fillMaxWidth()
            .offset(y = (-12).dp)
    )
}

@Composable
private fun MultiSelectionDropdown(
    expanded: Boolean,
    maps: List<IMapFile>,
    actions: List<MapAction>,
    onDismissRequest: (clearSelection: Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    if (maps.isNotEmpty()) DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDismissRequest(false) }
    ) {
        actions.forEach { action ->
            DropdownMenuItem(
                text = { Text(stringResource(action.shortLabel)) },
                leadingIcon = {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null
                    )
                },
                colors = if (action.destructive) MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.error,
                    leadingIconColor = MaterialTheme.colorScheme.error
                ) else MenuDefaults.itemColors(),
                onClick = { scope.launch {
                    onDismissRequest(false)
                    action.execute(context, maps.toList(), DefaultMapActionArguments())
                    onDismissRequest(true) // clear selection
                } }
            )
        }
    }
}