package io.github.aliernfrog.pftool_shared.ui.dialog

import android.content.ClipData
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.pftool_shared.enum.DocumentsUIPackageMetadata
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.pftool_shared.util.getSharedString
import io.github.aliernfrog.pftool_shared.util.sharedStringResource
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.data.Social
import io.github.aliernfrog.shared.data.getIconPainter
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.FadeVisibility
import io.github.aliernfrog.shared.ui.component.IconButtonWithTooltip
import io.github.aliernfrog.shared.ui.component.ScrollableRow
import io.github.aliernfrog.shared.util.sharedStringResource
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.extension.resolveString
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DocumentsUINotFoundDialog(
    onDismissRequest: () -> Unit,
    supportLinks: List<Social>
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val topToastState = koinInject<TopToastState>()

    var supportLinksExpanded by remember { mutableStateOf(false) }
    val expandArrowRotation by animateFloatAsState(
        if (supportLinksExpanded) 180f else 0f
    )

    // I do not know if disabling DocumentsUI is even possible, so this may never be true.
    val isDocumentsUiAvailable = remember {
        PFToolSharedUtil.getDocumentsUIPackageInfo(context) != null
    }

    @Composable
    fun DismissButton() {
        TextButton(
            onClick = onDismissRequest,
            shapes = ButtonDefaults.shapes()
        ) {
            Text(sharedStringResource(SharedString::actionDismiss))
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            if (isDocumentsUiAvailable) Button(
                onClick = {
                    PFToolSharedUtil.launchDocumentsUIAppInfoPage(context)
                    onDismissRequest()
                },
                shapes = ButtonDefaults.shapes()
            ) {
                ButtonIcon(
                    painter = rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew)
                )
                Text(sharedStringResource(PFToolSharedString::permissionsSAFDocumentsUiNotFoundOpenAppInfo))
            }
            else DismissButton()
        },
        dismissButton = if (isDocumentsUiAvailable) { {
            DismissButton()
        } } else null,
        title = {
            Text(sharedStringResource(PFToolSharedString::permissionsSAFDocumentsUiNotFound))
                },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = sharedStringResource(
                        if (isDocumentsUiAvailable) PFToolSharedString::permissionsSAFDocumentsUiNotFoundDisabled
                        else PFToolSharedString::permissionsSAFDocumentsUiNotFoundUninstalled
                    )
                )

                if (!isDocumentsUiAvailable) DocumentsUIPackageMetadata.entries.forEach { metadata ->
                    val command = "adb shell pm install-existing ${metadata.packageName}"
                    val onClick: () -> Unit = { scope.launch {
                        clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(
                            /* label = */ null, /* text = */ command
                        )))
                        topToastState.showAndroidToast(
                            text = context.getSharedString(PFToolSharedString::infoCopied),
                            icon = Icons.Default.ContentCopy
                        )
                    } }

                    Card(
                        onClick = onClick,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            IconButtonWithTooltip(
                                icon = rememberVectorPainter(Icons.Rounded.ContentCopy),
                                contentDescription = sharedStringResource(PFToolSharedString::actionCopy),
                                modifier = Modifier.padding(end = 2.dp),
                                onClick = onClick
                            )
                            Text(
                                text = command,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Card(
                    onClick = { supportLinksExpanded = !supportLinksExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = sharedStringResource(PFToolSharedString::permissionsSAFDocumentsUiNotFoundHelp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(end = 8.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.rotate(expandArrowRotation)
                        )
                    }

                    FadeVisibility(
                        visible = supportLinksExpanded
                    ) {
                        ScrollableRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                            gradientColor = CardDefaults.cardColors().containerColor,
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        ) {
                            supportLinks.forEach { social ->
                                OutlinedButton(
                                    onClick = { uriHandler.openUri(social.url) },
                                    shapes = ButtonDefaults.shapes()
                                ) {
                                    ButtonIcon(social.getIconPainter())
                                    Text(social.label.resolveString())
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}