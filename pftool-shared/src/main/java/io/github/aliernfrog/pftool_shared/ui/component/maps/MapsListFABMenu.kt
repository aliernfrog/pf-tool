package io.github.aliernfrog.pftool_shared.ui.component.maps

import android.content.Intent
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.SdCard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.pftool_shared.data.FileExtension
import io.github.aliernfrog.pftool_shared.impl.FileWrapper
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.pftool_shared.util.getSharedString
import io.github.aliernfrog.pftool_shared.util.sharedStringResource
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.ToggleFloatingActionButton
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.extension.showErrorToast
import io.github.aliernfrog.shared.util.getSharedString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File

@Composable
fun MapsListFABMenu(
    expanded: Boolean,
    supportedFileExtensions: List<FileExtension>,
    modifier: Modifier = Modifier,
    fixOffset: Boolean = true,
    onExpandedStateChange: (Boolean) -> Unit,
    onMapPick: (FileWrapper) -> Unit,
    button: @Composable () -> Unit = {
        MapsListFABToggleButton(
            expanded = expanded,
            onExpandedStateChange = onExpandedStateChange
        )
    }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val topToastState = koinInject<TopToastState>()
    var downloadDialogShown by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data?.data != null) scope.launch {
            withContext(Dispatchers.IO) {
                val cachedFile = PFToolSharedUtil.cacheFile(
                    uri = it.data?.data!!,
                    parentName = "maps",
                    context = context
                )
                if (cachedFile != null) onMapPick(FileWrapper(cachedFile))
                else topToastState.showErrorToast(
                    text = context.getSharedString(PFToolSharedString::mapsListPickMapFailed),
                )
            }
        }
    }

    FloatingActionButtonMenu(
        expanded = expanded,
        modifier = modifier
            .offset(
                x = if (fixOffset) 16.dp else 0.dp,
                y = if (fixOffset) 16.dp else 0.dp
            ),
        button = button
    ) {
        FloatingActionButtonMenuItem(
            onClick = {
                downloadDialogShown = true
                onExpandedStateChange(false)
            },
            text = { Text(sharedStringResource(PFToolSharedString::mapsListAddURL)) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Download,
                    contentDescription = null
                )
            }
        )
        FloatingActionButtonMenuItem(
            onClick = {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                    .setType("* / *")
                    .putExtra(
                        Intent.EXTRA_MIME_TYPES,
                        supportedFileExtensions.map { it.mimeType }.toTypedArray()
                    )
                launcher.launch(intent)
                onExpandedStateChange(false)
            },
            text = { Text(sharedStringResource(PFToolSharedString::mapsListAddStorage)) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.SdCard,
                    contentDescription = null
                )
            }
        )
    }

    if (downloadDialogShown) DownloadMapFromURLDialog(
        onDismissRequest = { downloadDialogShown = false },
        supportedFileExtensions = supportedFileExtensions,
        onDownloadFinish = { file ->
            file?.let {
                onMapPick(FileWrapper(it))
            } ?: topToastState.showErrorToast(
                context.getSharedString(SharedString::warningError)
            )
            downloadDialogShown = false
        }
    )
}

@Composable
fun MapsListFABToggleButton(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    onExpandedStateChange: (Boolean) -> Unit
) {
    ToggleFloatingActionButton(
        checked = expanded,
        onCheckedChange = onExpandedStateChange,
        modifier = modifier
    ) {
        val iconRotation by animateFloatAsState(
            if (expanded) 45f else 0f
        )
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = if (!expanded) sharedStringResource(PFToolSharedString::mapsListAdd)
            else io.github.aliernfrog.shared.util.sharedStringResource(SharedString::actionClose),
            modifier = Modifier.graphicsLayer {
                rotationZ = iconRotation
            }
        )
    }
}

@Composable
private fun DownloadMapFromURLDialog(
    onDismissRequest: () -> Unit,
    supportedFileExtensions: List<FileExtension>,
    onDownloadFinish: (File?) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    var url by rememberSaveable { mutableStateOf("") }
    var progress by rememberSaveable { mutableStateOf<Float?>(null) }
    var clipboardUrl by rememberSaveable { mutableStateOf<String?>(null) }

    fun isValidURL(string: String): Boolean =
        string.isNotEmpty() && Patterns.WEB_URL.matcher(string).matches()

    val isUrlValid by remember(url) {
        derivedStateOf { isValidURL(url) }
    }

    val isDownloading by remember {
        derivedStateOf {
            progress != null
        }
    }
    val downloadButtonContentOpacity by animateFloatAsState(
        if (isDownloading) 0f else 1f
    )

    LaunchedEffect(Unit) {
        val clipboardText = clipboard.getClipEntry()?.clipData?.let { data ->
            if (data.itemCount > 0) {
                data.getItemAt(0).coerceToText(context).toString()
            } else null
        }
        if (clipboardText != null && isValidURL(clipboardText))
            clipboardUrl = clipboardText
    }

    AlertDialog(
        properties = DialogProperties(
            dismissOnBackPress = !isDownloading,
            dismissOnClickOutside = !isDownloading
        ),
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Crossfade(isUrlValid && !isDownloading) { enabled ->
                Button(
                    onClick = {
                        if (!enabled) return@Button
                        scope.launch(Dispatchers.IO) {
                            PFToolSharedUtil.cacheFile(
                                uri = url.toUri(),
                                parentName = "downloaded_maps",
                                context = context,
                                onProgress = { progress = it }
                            ).let {
                                onDownloadFinish(it)
                            }
                        }
                    },
                    shapes = ButtonDefaults.shapes(),
                    enabled = enabled
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.alpha(downloadButtonContentOpacity)
                        ) {
                            ButtonIcon(rememberVectorPainter(Icons.Default.Download))
                            Text(sharedStringResource(PFToolSharedString::actionDownload))
                        }
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(18.dp)
                                .alpha(1f - downloadButtonContentOpacity),
                            progress = { progress ?: 0f },
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        },
        dismissButton = {
            Crossfade(!isDownloading) { enabled ->
                TextButton(
                    onClick = {
                        if (enabled) onDismissRequest()
                    },
                    shapes = ButtonDefaults.shapes(),
                    enabled = enabled
                ) {
                    Text(
                        io.github.aliernfrog.shared.util.sharedStringResource(SharedString::actionCancel)
                    )
                }
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Download,
                contentDescription = null
            )
        },
        title = {
            Text(sharedStringResource(PFToolSharedString::mapsListDownload))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isDownloading,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null
                        )
                    },
                    placeholder = {
                        Text(sharedStringResource(PFToolSharedString::mapsListDownloadPlaceholder))
                    },
                    supportingText = {
                        Text(
                            sharedStringResource(PFToolSharedString::mapsListDownloadFooter)
                                .format(supportedFileExtensions.joinToString { it.extension })
                        )
                    }
                )

                clipboardUrl?.let { clipUrl ->
                    SuggestionChip(
                        modifier = Modifier.padding(top = 8.dp),
                        onClick = {
                            url = clipUrl
                            clipboardUrl = null
                        },
                        label = {
                            Text(
                                text = clipUrl,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = sharedStringResource(PFToolSharedString::mapsListDownloadClipboard)
                            )
                        }
                    )
                }
            }
        }
    )
}