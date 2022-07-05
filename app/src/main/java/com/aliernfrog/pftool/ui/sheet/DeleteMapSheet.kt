package com.aliernfrog.pftool.ui.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.composable.PFToolButtonCentered
import com.aliernfrog.pftool.ui.composable.PFToolColumnRounded
import com.aliernfrog.pftool.ui.composable.PFToolRoundedModalBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteMapSheet(mapName: String?, state: ModalBottomSheetState, onCancel: (() -> Unit)? = null, onDeleteConfirm: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    PFToolRoundedModalBottomSheet(sheetState = state) {
        PFToolColumnRounded {
            Text(text = context.getString(R.string.info_deleteQuestion).replace("%NAME%", mapName ?: ""), fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        Row {
            Column(Modifier.weight(1f)) {
                PFToolButtonCentered(title = context.getString(R.string.action_cancel)) {
                    scope.launch { state.hide() }
                    if (onCancel != null) onCancel()
                }
            }
            Column(Modifier.weight(1f)) {
                PFToolButtonCentered(title = context.getString(R.string.action_delete), backgroundColor = MaterialTheme.colors.error, contentColor = MaterialTheme.colors.onError) {
                    scope.launch { state.hide() }
                    onDeleteConfirm()
                }
            }
        }
    }
}