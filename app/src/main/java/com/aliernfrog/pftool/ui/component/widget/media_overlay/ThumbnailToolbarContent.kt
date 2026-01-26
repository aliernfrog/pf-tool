package com.aliernfrog.pftool.ui.component.widget.media_overlay

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import com.aliernfrog.pftool.R
import io.github.aliernfrog.shared.ui.component.ButtonIcon

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ThumbnailToolbarContent(
    onShareRequest: () -> Unit
) {
    FilledTonalButton(
        onClick = onShareRequest,
        shapes = ButtonDefaults.shapes()
    ) {
        ButtonIcon(
            painter = rememberVectorPainter(Icons.Default.Share),
            contentDescription = null
        )
        Text(stringResource(R.string.action_share))
    }
}