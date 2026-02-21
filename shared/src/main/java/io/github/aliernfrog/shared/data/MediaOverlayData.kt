package io.github.aliernfrog.shared.data

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import io.github.aliernfrog.shared.ui.component.ErrorWithIcon
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.sharedStringResource

data class MediaOverlayData(
    val model: Any?,
    val title: String? = null,
    val zoomEnabled: Boolean = true,
    val errorContent: @Composable () -> Unit = {
        ErrorWithIcon(
            description = sharedStringResource(SharedString::warningError),
            icon = rememberVectorPainter(Icons.Rounded.Error),
            contentColor = Color.Red
        )
    },
    val toolbarContent: (@Composable RowScope.() -> Unit)? = null,
    val optionsSheetContent: (@Composable ColumnScope.() -> Unit)? = null
)