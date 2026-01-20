package io.github.aliernfrog.shared.ui.screen

import android.content.ClipData
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.shared.data.Social
import io.github.aliernfrog.shared.data.getIconPainter
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppSmallTopBar
import io.github.aliernfrog.shared.ui.component.ErrorWithIcon
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.ui.component.form.DividerRow
import io.github.aliernfrog.shared.ui.component.form.ExpandableRow
import io.github.aliernfrog.shared.ui.component.verticalSegmentedShape
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.extension.resolveString
import io.github.aliernfrog.shared.util.sharedStringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CrashHandlerScreen(
    stackTrace: String,
    debugInfo: String,
    supportLinks: List<Social>
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

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

            ExpressiveButtonRow(
                title = sharedStringResource(SharedString.CrashHandlerSendReport),
                description = sharedStringResource(SharedString.CrashHandlerSendReportDescription),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = {
                    ExpressiveRowIcon(
                        painter = rememberVectorPainter(Icons.AutoMirrored.Rounded.Send)
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .verticalSegmentedShape(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
            ) {
                Toast.makeText(context, "Soon (TM)", Toast.LENGTH_SHORT).show()
            }

            DividerRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            var supportSectionExpanded by remember { mutableStateOf(false) }
            ExpandableRow(
                expanded = supportSectionExpanded,
                onClickHeader = { supportSectionExpanded = !supportSectionExpanded },
                title = sharedStringResource(SharedString.CrashHandlerSupport),
                description = if (supportSectionExpanded) null else sharedStringResource(
                    SharedString.CrashHandlerSupportDescription
                ),
                icon = {
                    ExpressiveRowIcon(
                        painter = rememberVectorPainter(Icons.Rounded.QuestionMark)
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .verticalSegmentedShape()
            ) {
                val supportLinkButtons: List<@Composable () -> Unit> = supportLinks.map { social -> {
                    ExpressiveButtonRow(
                        title = social.label.resolveString(),
                        icon = {
                            ExpressiveRowIcon(
                                painter = social.getIconPainter()
                            )
                        }
                    ) {
                        uriHandler.openUri(social.url)
                    }
                } }

                VerticalSegmentor(
                    {
                        Text(
                            text = sharedStringResource(SharedString.CrashHandlerSupportGuide),
                            style = MaterialTheme.typography.bodySmallEmphasized,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    },
                    {
                        ExpressiveButtonRow(
                            title = sharedStringResource(SharedString.CrashHandlerSupportCopyDetails),
                            icon = {
                                ExpressiveRowIcon(
                                    painter = rememberVectorPainter(Icons.Rounded.ContentCopy)
                                )
                            }
                        ) {
                            scope.launch {
                                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(
                                    /* label = */ null,
                                    /* text = */ debugInfo + "\n\n" + stackTrace
                                )))
                            }
                        }
                    },
                    *supportLinkButtons.toTypedArray(),
                    modifier = Modifier.padding(
                        start = 12.dp, end = 12.dp, bottom = 12.dp
                    )
                )
            }

            ExpressiveSection(
                title = sharedStringResource(SharedString.CrashHandlerStackTrace)
            ) {
                ElevatedCard(
                    modifier = Modifier.padding(
                        horizontal = 12.dp
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