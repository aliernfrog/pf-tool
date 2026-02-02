package io.github.aliernfrog.shared.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SizedButton(
    onClick: () -> Unit,
    size: Dp,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.(
        textStyle: TextStyle,
        iconSpacing: Dp,
        iconSize: Dp
            ) -> Unit
) {
    Button(
        onClick = onClick,
        shapes = ButtonDefaults.shapesFor(size),
        contentPadding = ButtonDefaults.contentPaddingFor(size),
        enabled = enabled,
        modifier = modifier.sizeIn(size)
    ) {
        content(
            ButtonDefaults.textStyleFor(size),
            ButtonDefaults.iconSpacingFor(size),
            ButtonDefaults.iconSizeFor(size)
        )
    }
}