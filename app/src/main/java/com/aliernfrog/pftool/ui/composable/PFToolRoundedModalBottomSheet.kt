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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliernfrog.pftool.ui.theme.sheetHandleBar
import com.aliernfrog.pftool.ui.theme.sheetScrim

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PFToolRoundedModalBottomSheet(title: String? = null, sheetState: ModalBottomSheetState, sheetContent: @Composable ColumnScope.() -> Unit) {
    ModalBottomSheetLayout(
        sheetBackgroundColor = Color(0x00000000),
        sheetContentColor = MaterialTheme.colors.onBackground,
        sheetState = sheetState,
        scrimColor = MaterialTheme.colors.sheetScrim,
        sheetElevation = 0.dp,
        content = {},
        sheetContent = {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .background(MaterialTheme.colors.sheetHandleBar, shape = RoundedCornerShape(20.dp))
                    .width(50.dp)
                    .height(5.dp)
                    .align(Alignment.CenterHorizontally)
                )
                Column(Modifier
                    .widthIn(0.dp, 600.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colors.background).padding(horizontal = 24.dp)) {
                    if (title != null) Text(text = title, fontSize = 30.sp, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp).align(Alignment.CenterHorizontally))
                    sheetContent()
                }
            }
        }
    )
}