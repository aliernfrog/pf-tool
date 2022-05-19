package com.aliernfrog.pftool.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PFToolRoundedModalBottomSheet(sheetContent: @Composable ColumnScope.() -> Unit, ) {
    ModalBottomSheetLayout(
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetContentColor = MaterialTheme.colors.onBackground,
        sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded),
        content = {},
        sheetContent = {
            Box(modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .background(MaterialTheme.colors.onBackground, shape = RoundedCornerShape(20.dp))
                .width(30.dp)
                .height(5.dp)
                .align(Alignment.CenterHorizontally)
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())) {
                Column(Modifier.padding(horizontal = 24.dp), content = sheetContent)
            }
        }
    )
}