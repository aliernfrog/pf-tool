package com.aliernfrog.pftool.ui.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.aliernfrog.pftool.ui.composable.PFToolModalBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteMapSheet(mapName: String?, state: ModalBottomSheetState, onCancel: (() -> Unit)? = null, onDeleteConfirm: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    PFToolModalBottomSheet(sheetState = state) {
        PFToolColumnRounded {
            Text(text = context.getString(R.string.info_deleteQuestion).replace("%NAME%", mapName ?: ""), color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        Row {
            Column(Modifier.weight(1f)) {
                PFToolButtonCentered(title = context.getString(R.string.action_cancel)) {
                    scope.launch { state.hide() }
                    if (onCancel != null) onCancel()
                }
            }
            Column(Modifier.weight(1f)) {
                PFToolButtonCentered(title = context.getString(R.string.action_delete), containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer) {
                    scope.launch { state.hide() }
                    onDeleteConfirm()
                }
            }
        }
    }
}