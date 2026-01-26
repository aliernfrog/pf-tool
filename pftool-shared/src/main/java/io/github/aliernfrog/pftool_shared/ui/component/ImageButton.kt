package io.github.aliernfrog.pftool_shared.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ImageButtonInfo(
    text: String,
    modifier: Modifier = Modifier,
    icon: Painter? = null
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        icon?.let {
            val density = LocalDensity.current
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(
                    with(density) {
                        18.sp.toDp()
                    }
                )
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}