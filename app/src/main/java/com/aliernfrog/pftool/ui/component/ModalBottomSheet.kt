package com.aliernfrog.pftool.ui.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliernfrog.pftool.AppRoundnessSize

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ModalBottomSheet(
    title: String? = null,
    sheetState: ModalBottomSheetState,
    sheetScrollState: ScrollState = rememberScrollState(),
    sheetContent: @Composable ColumnScope.() -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    ModalBottomSheetLayout(
        sheetBackgroundColor = Color.Transparent,
        sheetContentColor = MaterialTheme.colorScheme.onBackground,
        sheetState = sheetState,
        sheetElevation = 0.dp,
        content = {},
        sheetContent = {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = AppRoundnessSize, topEnd = AppRoundnessSize))
                    .background(MaterialTheme.colorScheme.background)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(32.dp, 4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        .align(Alignment.CenterHorizontally)
                )
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = AppRoundnessSize, topEnd = AppRoundnessSize)).verticalScroll(sheetScrollState).navigationBarsPadding()) {
                    if (title != null) Text(text = title, fontSize = 30.sp, modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally))
                    sheetContent()
                }
            }
        }
    )

    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible) keyboardController?.hide()
    }
}