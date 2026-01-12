package io.github.aliernfrog.shared.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pinch
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ZoomInMap
import androidx.compose.material.icons.rounded.HideImage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import io.github.aliernfrog.shared.ui.component.form.DividerRow
import io.github.aliernfrog.shared.data.MediaOverlayData
import io.github.aliernfrog.shared.ui.viewmodel.InsetsViewModel
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import io.github.aliernfrog.shared.util.sharedStringResource
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.koin.androidx.compose.koinViewModel
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaOverlay(
    data: MediaOverlayData,
    showMediaOverlayGuidePref: BasePreferenceManager.Preference<Boolean>,
    onDismissRequest: () -> Unit
) {
    val insetsViewModel = koinViewModel<InsetsViewModel>()

    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val zoomState = rememberZoomState()
    val isZoomedIn = zoomState.scale > 1f
    val isImeVisible = insetsViewModel.isImeVisible
    val bottomSheetState = rememberStandardBottomSheetState(
        skipHiddenState = false
    )

    var state by remember { mutableStateOf(
        if (data.model != null) MediaOverlayState.SUCCESS else MediaOverlayState.NO_IMAGE
    ) }
    var showOverlay by remember { mutableStateOf(false) }
    var viewportHeight by remember { mutableStateOf(0.dp) }
    var optionsHeight by remember { mutableStateOf(viewportHeight/3) }
    var offsetY by remember { mutableStateOf(0.dp) }
    val animatedOffsetY by animateDpAsState(offsetY)
    val overlayCanBeShown = !isZoomedIn && offsetY == 0.dp
    val sheetPeekHeight = (viewportHeight/3).let { maxPeekHeight ->
        if (optionsHeight == 0.dp || optionsHeight > maxPeekHeight) maxPeekHeight
        else optionsHeight
    }

    BackHandler {
        if (isZoomedIn) scope.launch { zoomState.reset() }
        else onDismissRequest()
    }

    LaunchedEffect(overlayCanBeShown) {
        showOverlay = overlayCanBeShown
        if (data.optionsSheetContent != null && overlayCanBeShown && bottomSheetState.targetValue == SheetValue.Hidden) bottomSheetState.partialExpand()
    }

    LaunchedEffect(showOverlay) {
        if (data.optionsSheetContent == null) return@LaunchedEffect bottomSheetState.hide()
        if (showOverlay && overlayCanBeShown && bottomSheetState.targetValue == SheetValue.Hidden) bottomSheetState.partialExpand()
        else if (!showOverlay && bottomSheetState.targetValue != SheetValue.Hidden) bottomSheetState.hide()
    }

    // Expand sheet to add IME padding, if IME is shown
    LaunchedEffect(isImeVisible) {
        if (isImeVisible) bottomSheetState.expand()
    }

    BottomSheetScaffold(
        sheetContent = {
            data.optionsSheetContent?.let {
                Column(
                    modifier = Modifier
                        .onSizeChanged { size ->
                            with(density) {
                                optionsHeight =
                                    size.height.toDp() + 48.dp // 48dp drag handle height
                            }
                        }
                        .padding(bottom = 8.dp)
                        .navigationBarsPadding()
                        .imePadding()
                        .verticalScroll(rememberScrollState()),
                    content = it
                )
            }
        },
        sheetPeekHeight = sheetPeekHeight,
        scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = bottomSheetState
        ),
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                with(density) {
                    viewportHeight = it.height.toDp()
                }
            },
        containerColor = Color.Black.copy(
            alpha = (viewportHeight.value/offsetY.value.absoluteValue/11)
        ),
        contentColor = Color.White
    ) {
        Box {
            FadeVisibility(visible = showOverlay && overlayCanBeShown,
                modifier = Modifier.zIndex(1f)) {
                Row(modifier = Modifier.background(Color.Black.copy(alpha = 0.7f))
                    .statusBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                    Row(modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically) {
                        IconButton(shapes = IconButtonDefaults.shapes(),
                            onClick = onDismissRequest,
                            modifier = Modifier.padding(8.dp)) {
                            Icon(imageVector = Icons.Default.Close,
                                contentDescription = sharedStringResource(SharedString.ActionClose))
                        }
                        data.title?.let {
                            Text(text = it,
                                style = MaterialTheme.typography.titleLarge,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 2)
                        }
                    }
                    IconButton(shapes = IconButtonDefaults.shapes(),
                        onClick = { showMediaOverlayGuidePref.value = true },
                        modifier = Modifier.padding(8.dp)) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = sharedStringResource(SharedString.MediaOverlayGuide))
                    }
                }
            }
            Crossfade(
                targetState = state,
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(x = 0, y = animatedOffsetY.roundToPx()) }
                    .pointerInput(Unit) {
                        if (!isZoomedIn) detectVerticalDragGestures(
                            onDragEnd = {
                                if (zoomState.scale <= 1f && offsetY.value.absoluteValue > viewportHeight.value / 6) {
                                    val isPositiveOffset = offsetY.value > 0
                                    val absOffsetToSet = (viewportHeight/(1.6.dp)).dp
                                    onDismissRequest()
                                    offsetY = if (isPositiveOffset) absOffsetToSet else -absOffsetToSet
                                } else offsetY = 0.dp
                            },
                            onVerticalDrag = { _, dragAmount ->
                                if (zoomState.scale <= 1f) with(density) {
                                    offsetY += dragAmount.toDp()
                                }
                            }
                        )
                    }
            ) {
                @Composable
                fun CenteredBox(
                    modifier: Modifier = Modifier,
                    content: @Composable BoxScope.() -> Unit
                ) {
                    Box(
                        modifier = modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                        content = content
                    )
                }

                when (it) {
                    MediaOverlayState.ERROR -> CenteredBox {
                        data.errorContent()
                    }
                    MediaOverlayState.NO_IMAGE -> CenteredBox {
                        ErrorWithIcon(
                            error = "",
                            painter = rememberVectorPainter(Icons.Rounded.HideImage),
                            contentColor = Color.White
                        )
                    }
                    MediaOverlayState.SUCCESS -> AsyncImage(
                        model = data.model,
                        onError = { state = MediaOverlayState.ERROR },
                        onSuccess = { state = MediaOverlayState.SUCCESS },
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .zoomable(
                                zoomState = zoomState,
                                zoomEnabled = data.zoomEnabled,
                                onTap = { _ ->
                                    showOverlay = !showOverlay && overlayCanBeShown
                                }
                            )
                    )
                }
            }
            data.toolbarContent?.let { toolbarContent ->
                AnimatedVisibility(
                    visible = showOverlay,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .systemBarsPadding()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    HorizontalFloatingToolbar(
                        expanded = true,
                        content = toolbarContent
                    )
                }
            }
        }
    }

    if (showMediaOverlayGuidePref.value) GuideDialog(
        onDismissRequest = {
            showMediaOverlayGuidePref.value = false
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GuideDialog(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                shapes = ButtonDefaults.shapes(),
                onClick = onDismissRequest
            ) {
                Text(sharedStringResource(SharedString.ActionOK))
            }
        },
        text = {
            Column {
                listOf(
                    Icons.Default.TouchApp to SharedString.MediaOverlayGuideToggleOverlay,
                    Icons.Default.ZoomInMap to SharedString.MediaOverlayGuideToggleZoom,
                    Icons.Default.Pinch to SharedString.MediaOverlayGuideZoom
                ).forEachIndexed { index, pair ->
                    if (index != 0) DividerRow(Modifier.fillMaxWidth())
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = pair.first,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Text(sharedStringResource(pair.second))
                    }
                }
            }
        }
    )
}

private enum class MediaOverlayState {
    SUCCESS,
    ERROR,
    NO_IMAGE
}