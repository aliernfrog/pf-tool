package com.aliernfrog.pftool.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliernfrog.pftool.util.GeneralUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PFToolModalBottomSheet(title: String? = null, sheetState: ModalBottomSheetState, sheetScrollState: ScrollState = rememberScrollState(), sheetContent: @Composable ColumnScope.() -> Unit) {
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetBackgroundColor = Color(0x00000000),
        sheetContentColor = MaterialTheme.colorScheme.onBackground,
        sheetState = sheetState,
        sheetElevation = 0.dp,
        content = {},
        sheetContent = {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.fillMaxWidth().height(GeneralUtil.getStatusBarHeight()+60.dp).clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { scope.launch { sheetState.hide() } }
                ))
                Box(modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .background(Color.White, shape = RoundedCornerShape(20.dp))
                    .width(50.dp)
                    .height(4.dp)
                    .align(Alignment.CenterHorizontally)
                )
                Column(Modifier
                    .widthIn(0.dp, 600.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .verticalScroll(sheetScrollState)
                    .background(MaterialTheme.colorScheme.background).padding(horizontal = 24.dp)) {
                    if (title != null) Text(text = title, fontSize = 30.sp, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp).align(Alignment.CenterHorizontally))
                    sheetContent()
                    Spacer(modifier = Modifier.height(GeneralUtil.getNavigationBarHeight()))
                }
            }
        }
    )
}