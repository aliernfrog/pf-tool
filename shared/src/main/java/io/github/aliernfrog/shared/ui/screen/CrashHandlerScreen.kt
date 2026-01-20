package io.github.aliernfrog.shared.ui.screen

import android.content.ClipData
import android.os.Build
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.shared.data.Social
import io.github.aliernfrog.shared.data.getIconPainter
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppSmallTopBar
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.ErrorWithIcon
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.extension.resolveString
import io.github.aliernfrog.shared.util.sharedStringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CrashHandlerScreen(
    message: String,
    stackTrace: String,
    supportLinks: List<Social>
) {
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current
    val uriHandler = LocalUriHandler.current

    AppScaffold(
        topBar = { scrollBehavior ->
            AppSmallTopBar(
                title = sharedStringResource(SharedString.CrashHandlerTitle),
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
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                ErrorWithIcon(
                    error = sharedStringResource(SharedString.CrashHandlerDescription),
                    painter = rememberVectorPainter(Icons.Default.Error),
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    textOpacity = 1f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = sharedStringResource(SharedString.CrashHandlerReport),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            ExpressiveSection(
                title = sharedStringResource(SharedString.CrashHandlerReportManually)
            ) {
                FilledTonalButton(
                    onClick = { scope.launch {
                        clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(
                            /* label = */ null,
                            /* text = */ "Android SDK ${Build.VERSION.SDK_INT}" + "\n" + stackTrace
                        )))
                    } },
                    shapes = ButtonDefaults.shapes(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    ButtonIcon(rememberVectorPainter(Icons.Default.ContentCopy))
                    Text(sharedStringResource(SharedString.CrashHandlerReportManuallyCopyDetails))
                }

                Text(
                    text = sharedStringResource(SharedString.CrashHandlerReportManuallyGuide),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                supportLinks.forEach { social ->
                    OutlinedButton(
                        onClick = { uriHandler.openUri(social.url) },
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        ButtonIcon(social.getIconPainter())
                        Text(social.label.resolveString())
                    }
                }
            }

            ExpressiveSection(
                title = sharedStringResource(SharedString.CrashHandlerStackTrace)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmallEmphasized,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                ElevatedCard(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                ) {
                    SelectionContainer(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stackTrace,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}