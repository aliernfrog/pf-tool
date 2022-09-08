package com.aliernfrog.pftool.ui.sheet

import android.content.Context
import android.content.Intent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolButton
import com.aliernfrog.pftool.ui.composable.PFToolModalBottomSheet
import com.aliernfrog.pftool.util.FileUtil
import com.aliernfrog.toptoast.TopToastColorType
import com.aliernfrog.toptoast.TopToastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExportedMapSheet(map: File?, topToastManager: TopToastManager, state: ModalBottomSheetState, onFileChange: () -> Unit) {
    if (map != null) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val deleteMapSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
        PFToolModalBottomSheet(title = map.nameWithoutExtension, sheetState = state) {
            MapActions(map, state, deleteMapSheetState)
        }
        DeleteMapSheet(mapName = map.nameWithoutExtension, state = deleteMapSheetState, onCancel = {
            scope.launch { state.show() }
        }) {
            deleteMap(map, topToastManager, context, onFileChange)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MapActions(map: File, state: ModalBottomSheetState, deleteMapSheetState: ModalBottomSheetState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    PFToolButton(
        title = context.getString(R.string.manageMapsShare),
        painter = painterResource(id = R.drawable.share),
        backgroundColor = MaterialTheme.colors.secondary,
        contentColor = MaterialTheme.colors.onSecondary) {
        shareMap(map, scope, state, context)
    }
    PFToolButton(
        title = context.getString(R.string.manageMapsDelete),
        painter = painterResource(id = R.drawable.trash),
        backgroundColor = MaterialTheme.colors.error,
        contentColor = MaterialTheme.colors.onError,
        painterTintColor = null,
        painterBackgroundColor = Color.Black
    ) {
        scope.launch {
            state.hide()
            deleteMapSheetState.show()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun shareMap(map: File, scope: CoroutineScope, state: ModalBottomSheetState, context: Context) {
    scope.launch { state.hide() }
    val intent = FileUtil.shareFile(map.absolutePath, "application/zip", context)
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.action_share)))
}

private fun deleteMap(map: File, topToastManager: TopToastManager, context: Context, onFileChange: () -> Unit) {
    map.delete()
    onFileChange()
    topToastManager.showToast(context.getString(R.string.info_done), iconDrawableId = R.drawable.check, iconBackgroundColorType = TopToastColorType.PRIMARY)
}