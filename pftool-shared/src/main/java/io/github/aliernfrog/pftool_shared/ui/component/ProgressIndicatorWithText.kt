package io.github.aliernfrog.pftool_shared.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.pftool_shared.impl.Progress
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.sharedStringResource

@Composable
fun HorizontalProgressIndicatorWithText(
    progress: Progress?,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified,
    indicatorColor: Color? = null
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        CircularProgress(
            progress = progress,
            color = indicatorColor
        )
        Text(
            text = progress?.description ?: sharedStringResource(SharedString.InfoPleaseWait),
            color = textColor
        )
    }
}

@Composable
fun VerticalProgressIndicatorWithText(
    progress: Progress?,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified,
    indicatorColor: Color? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        CircularProgress(
            progress = progress,
            color = indicatorColor
        )
        Text(
            text = progress?.description ?: sharedStringResource(SharedString.InfoPleaseWait),
            color = textColor,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CircularProgress(
    progress: Progress?,
    modifier: Modifier = Modifier,
    color: Color? = null
) {
    progress?.float.let {
        if (it == null || progress?.finished == true) return@let CircularWavyProgressIndicator(
            trackColor = color ?: ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            modifier = modifier
        )
        val animated by animateFloatAsState(it)
        CircularWavyProgressIndicator(
            progress = { animated },
            color = color ?: ProgressIndicatorDefaults.circularColor,
            modifier = modifier
        )
    }
}