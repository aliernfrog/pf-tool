package io.github.aliernfrog.shared.ui.screen

import android.content.ClipData
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.shared.data.Social
import io.github.aliernfrog.shared.data.getIconPainter
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppSmallTopBar
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.ErrorWithIcon
import io.github.aliernfrog.shared.ui.component.SizedButton
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.ui.component.form.DividerRow
import io.github.aliernfrog.shared.ui.component.form.ExpandableRow
import io.github.aliernfrog.shared.ui.component.verticalSegmentedShape
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.TAG
import io.github.aliernfrog.shared.util.extension.resolveString
import io.github.aliernfrog.shared.util.getSharedString
import io.github.aliernfrog.shared.util.sharedStringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CrashHandlerScreen(
    crashReportURL: String,
    stackTrace: String,
    debugInfo: String,
    supportLinks: List<Social>,
    onRestartAppRequest: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    var reportState by remember {
        mutableStateOf<ReportState>(ReportState.NotSent)
    }

    val getCrashDetails = {
        debugInfo + "\n\n" + stackTrace
    }

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

            Text(
                text = sharedStringResource(SharedString.CrashHandlerReport),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            )

            SizedButton(
                onClick = {
                    reportState = ReportState.Sending
                    scope.launch(Dispatchers.IO) {
                        val response = sendCrashReport(
                            toUrl = crashReportURL,
                            app = context.getSharedString(SharedString.AppName),
                            details = getCrashDetails()
                        )
                        scope.launch(Dispatchers.Main) {
                            Toast.makeText(context,
                                if (response is ReportState.Error) "${response.body} (${response.statusCode})"
                                else context.getSharedString(SharedString.CrashHandlerSendReportSent),
                                Toast.LENGTH_SHORT
                            ).show()
                            reportState = if (response is ReportState.Error) ReportState.NotSent else response
                        }
                    }
                },
                size = ButtonDefaults.MediumContainerHeight,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) { textStyle, iconSpacing, iconSize ->
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    val isSending = reportState is ReportState.Sending
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.alpha(
                            if (isSending) 0f else 1f
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(iconSize)
                        )
                        Spacer(Modifier.width(iconSpacing))
                        Text(
                            text = sharedStringResource(
                                if (reportState is ReportState.Sent) SharedString.CrashHandlerSendReportSent
                                else SharedString.CrashHandlerSendReport
                            ),
                            style = textStyle
                        )
                    }

                    if (isSending) CircularProgressIndicator(
                        modifier = Modifier.size(iconSize)
                    )
                }
            }

            OutlinedButton(
                onClick = onRestartAppRequest,
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            ) {
                ButtonIcon(rememberVectorPainter(Icons.Default.RestartAlt))
                Text(sharedStringResource(SharedString.CrashHandlerRestartApp))
            }

            DividerRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp)
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
                                    /* text = */ getCrashDetails()
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

private fun sendCrashReport(toUrl: String, app: String, details: String): ReportState {
    val json = JSONObject()
        .put("app", app)
        .put("details", details)

    return try {
        val url = URL(toUrl)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .method(
                method = "POST",
                body = json.toString().toRequestBody("application/json".toMediaType())
            )
            .build()
        client.newCall(request).execute().use { response ->
            if (response.code in 200..299) ReportState.Sent(
                statusCode = response.code,
                body = response.body?.string()
            ) else ReportState.Error(
                statusCode = response.code,
                body = response.body?.string()
            )
        }
    } catch (e: Exception) {
        Log.e(TAG, "sendPostRequest: ", e)
        ReportState.Error(null, null)
    }
}

private sealed class ReportState {
    data object NotSent : ReportState()
    data object Sending : ReportState()
    data class Error(val statusCode: Int?, val body: String?) : ReportState()
    data class Sent(val statusCode: Int, val body: String?) : ReportState()
}