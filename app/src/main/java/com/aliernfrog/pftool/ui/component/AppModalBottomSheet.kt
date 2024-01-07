package com.aliernfrog.pftool.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.ui.viewmodel.InsetsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

/*
// this is not yet used by the app, uncomment when needed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppModalBottomSheet(
    title: String? = null,
    sheetState: SheetState,
    sheetScrollState: ScrollState = rememberScrollState(),
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    sheetContent: @Composable ColumnScope.() -> Unit
) {
    BaseModalBottomSheet(
        sheetState = sheetState,
        dragHandle = dragHandle
    ) { bottomPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppBottomSheetShape)
                .verticalScroll(sheetScrollState)
                .padding(bottom = bottomPadding)
        ) {
            title?.let {
                Text(
                    text = it,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
                )
            }
            sheetContent()
        }
    }
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseModalBottomSheet(
    sheetState: SheetState,
    insetsViewModel: InsetsViewModel = getViewModel(),
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    content: @Composable ColumnScope.(bottomPadding: Dp) -> Unit
) {
    val scope = rememberCoroutineScope()
    if (sheetState.currentValue != SheetValue.Hidden || sheetState.targetValue != SheetValue.Hidden) ModalBottomSheet(
        onDismissRequest = { scope.launch {
            sheetState.hide()
        } },
        modifier = Modifier
            .padding(top = insetsViewModel.topPadding),
        sheetState = sheetState,
        dragHandle = dragHandle,
        windowInsets = WindowInsets(0.dp)
    ) {
        content(
            // Adding top padding since Modifier.padding causes an offset on the bottom sheet
            insetsViewModel.topPadding+insetsViewModel.bottomPadding
        )
    }
}

@Composable
fun SmallDragHandle() {
    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .size(32.dp, 4.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
    )
}