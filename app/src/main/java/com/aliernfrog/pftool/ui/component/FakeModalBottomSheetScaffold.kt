package com.aliernfrog.pftool.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FakeModalBottomSheetScaffold(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    content: @Composable (PaddingValues) -> Unit
) {
    val scope = rememberCoroutineScope()
    BottomSheetScaffold(
        sheetContent = sheetContent,
        modifier = modifier,
        scaffoldState = scaffoldState,
        content = {
            Box {
                content(it)
                AnimatedVisibility(
                    visible = scaffoldState.bottomSheetState.targetValue != SheetValue.Hidden,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BottomSheetDefaults.ScrimColor)
                            .clickable(
                                onClick = {
                                    scope.launch {
                                        scaffoldState.bottomSheetState.hide()
                                    }
                                },
                                interactionSource = null,
                                indication = null
                            )
                    )
                }
            }
        }
    )
}