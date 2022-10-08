package com.aliernfrog.pftool.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliernfrog.pftool.PFToolComposableShape
import com.aliernfrog.pftool.PFToolRoundnessSize
import com.aliernfrog.pftool.util.GeneralUtil

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PFToolModalBottomSheet(title: String? = null, sheetState: ModalBottomSheetState, sheetScrollState: ScrollState = rememberScrollState(), sheetContent: @Composable ColumnScope.() -> Unit) {
    ModalBottomSheetLayout(
        sheetBackgroundColor = Color.Transparent,
        sheetContentColor = MaterialTheme.colorScheme.onBackground,
        sheetState = sheetState,
        sheetElevation = 0.dp,
        content = {},
        sheetContent = {
            Column(modifier = Modifier.statusBarsPadding().fillMaxWidth().clip(RoundedCornerShape(topStart = PFToolRoundnessSize, topEnd = PFToolRoundnessSize)).background(MaterialTheme.colorScheme.background), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier
                    .padding(vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = PFToolComposableShape)
                    .size(30.dp, 5.dp)
                    .align(Alignment.CenterHorizontally)
                )
                Column(Modifier.verticalScroll(sheetScrollState).padding(horizontal = 24.dp)) {
                    if (title != null) Text(text = title, fontSize = 30.sp, modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally))
                    sheetContent()
                    Spacer(modifier = Modifier.height(GeneralUtil.getNavigationBarHeight()))
                }
            }
        }
    )
}