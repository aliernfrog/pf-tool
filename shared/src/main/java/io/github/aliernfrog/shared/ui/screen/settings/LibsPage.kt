package io.github.aliernfrog.shared.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import io.github.aliernfrog.shared.ui.component.AppModalBottomSheet
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppSmallTopBar
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.ui.component.form.DividerRow
import io.github.aliernfrog.shared.ui.viewmodel.settings.LibsPageViewModel
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.extension.horizontalFadingEdge
import io.github.aliernfrog.shared.util.sharedStringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LibsPage(
    vm: LibsPageViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val librarySheetState = rememberModalBottomSheetState()
    var selectedLibrary by remember { mutableStateOf<Library?>(null) }

    AppScaffold(
        topBar = { scrollBehavior ->
            AppSmallTopBar(
                title = sharedStringResource(SharedString.SettingsAboutLibs),
                scrollBehavior = scrollBehavior,
                onNavigationClick = onNavigateBackRequest
            )
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(vm.libraries) { index, lib ->
                val onClick: () -> Unit = {
                    scope.launch {
                        selectedLibrary = lib
                        librarySheetState.show()
                    }
                }

                val licenses = rememberSaveable {
                    lib.licenses.joinToString(", ") { it.name }
                }

                if (index != 0) DividerRow()
                ListItem(
                    overlineContent = { Text(lib.name) },
                    headlineContent = { lib.description?.let { Text(it) } },
                    supportingContent = {
                        SuggestionChip(
                            onClick = onClick,
                            label = { Text(licenses) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Balance,
                                    contentDescription = sharedStringResource(SharedString.SettingsAboutLibsLicense)
                                )
                            }
                        )
                    },
                    modifier = Modifier.clickable(onClick = onClick)
                )
            }

            item {
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }

    AppModalBottomSheet(sheetState = librarySheetState) {
        selectedLibrary?.let { lib ->
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = lib.name,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 16.dp)
                )
                lib.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 16.dp)
                    )
                }

                val buttonsScrollState = rememberScrollState()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .horizontalFadingEdge(
                            scrollState = buttonsScrollState,
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                            isRTL = LocalLayoutDirection.current == LayoutDirection.Rtl
                        )
                        .horizontalScroll(buttonsScrollState)
                        .padding(horizontal = 16.dp)
                ) {
                    lib.website?.let {
                        if (it.contains("://")) Button(
                            shapes = ButtonDefaults.shapes(),
                            onClick = { uriHandler.openUri(it) }
                        ) {
                            ButtonIcon(rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew))
                            Text(sharedStringResource(SharedString.SettingsAboutLibsWebsite))
                        }
                    }
                    lib.organization?.url?.let {
                        if (it.contains("://")) FilledTonalButton(
                            shapes = ButtonDefaults.shapes(),
                            onClick = {
                                uriHandler.openUri(it)
                            }
                        ) {
                            ButtonIcon(rememberVectorPainter(Icons.Default.CorporateFare))
                            Text(sharedStringResource(SharedString.SettingsAboutLibsOrganization))
                        }
                    }
                    lib.developers.forEach { dev ->
                        OutlinedButton(
                            shapes = ButtonDefaults.shapes(),
                            onClick = {
                                dev.organisationUrl?.let {
                                    if (it.contains("://")) uriHandler.openUri(it)
                                }
                            }
                        ) {
                            ButtonIcon(rememberVectorPainter(Icons.Default.Engineering))
                            Text(dev.name ?: "Developer")
                        }
                    }
                }
            }

            lib.licenses.forEach { license ->
                ExpressiveSection(
                    title = license.name
                ) {
                    Text(
                        text = license.licenseContent ?: "",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}