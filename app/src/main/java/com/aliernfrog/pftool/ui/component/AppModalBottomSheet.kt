package com.aliernfrog.pftool.ui.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliernfrog.pftool.ui.viewmodel.InsetsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

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
}

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