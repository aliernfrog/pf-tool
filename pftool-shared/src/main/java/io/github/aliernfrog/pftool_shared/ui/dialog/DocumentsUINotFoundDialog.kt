package io.github.aliernfrog.pftool_shared.ui.dialog

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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.pftool_shared.util.sharedStringResource
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.data.Social
import io.github.aliernfrog.shared.data.getIconPainter
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.FadeVisibility
import io.github.aliernfrog.shared.ui.component.ScrollableRow
import io.github.aliernfrog.shared.util.sharedStringResource
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.extension.resolveString

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DocumentsUINotFoundDialog(
    onDismissRequest: () -> Unit,
    supportLinks: List<Social>
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var supportLinksExpanded by remember { mutableStateOf(false) }
    val expandArrowRotation by animateFloatAsState(
        if (supportLinksExpanded) 180f else 0f
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = {
                    PFToolSharedUtil.launchDocumentsUIAppInfoPage(context)
                },
                shapes = ButtonDefaults.shapes()
            ) {
                ButtonIcon(
                    painter = rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew)
                )
                Text(sharedStringResource(PFToolSharedString::permissionsSAFDocumentsUiNotFoundOpenAppInfo))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(sharedStringResource(SharedString::actionCancel))
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = sharedStringResource(PFToolSharedString::permissionsSAFDocumentsUiNotFoundDescription),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Card(
                    onClick = { supportLinksExpanded = !supportLinksExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp).fillMaxWidth()
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
                        visible = supportLinksExpanded,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        ScrollableRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                            gradientColor = CardDefaults.cardColors().containerColor
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