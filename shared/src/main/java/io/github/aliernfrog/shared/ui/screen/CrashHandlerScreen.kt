package io.github.aliernfrog.shared.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.shared.data.Social
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppSmallTopBar
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.ErrorWithIcon
import io.github.aliernfrog.shared.ui.component.crash_handler.CrashDetails
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.sharedStringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CrashHandlerScreen(
    crashReportURL: String,
    stackTrace: String,
    debugInfo: String,
    supportLinks: List<Social>,
    onRestartAppRequest: () -> Unit
) {
    AppScaffold(
        topBar = { scrollBehavior ->
            AppSmallTopBar(
                title = sharedStringResource(SharedString::crashHandlerTitle),
                scrollBehavior = scrollBehavior
            )
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            ErrorWithIcon(
                description = sharedStringResource(SharedString::crashHandlerDescription),
                icon = rememberVectorPainter(Icons.Default.Error),
                contentColor = MaterialTheme.colorScheme.error,
                iconContainerColor = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            CrashDetails(
                crashReportURL = crashReportURL,
                stackTrace = stackTrace,
                debugInfo = debugInfo,
                supportLinks = supportLinks,
                secondaryOption = {
                    OutlinedButton(
                        onClick = onRestartAppRequest,
                        shapes = ButtonDefaults.shapes()
                    ) {
                        ButtonIcon(rememberVectorPainter(Icons.Default.RestartAlt))
                        Text(sharedStringResource(SharedString::crashHandlerRestartApp))
                    }
                }
            )
        }
    }
}