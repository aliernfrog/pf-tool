package com.aliernfrog.pftool.ui.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.crowdinURL
import com.aliernfrog.pftool.data.Language
import com.aliernfrog.pftool.languages
import com.aliernfrog.pftool.ui.component.BaseModalBottomSheet
import com.aliernfrog.pftool.ui.component.SmallDragHandle
import com.aliernfrog.pftool.ui.component.form.ButtonRow
import com.aliernfrog.pftool.ui.component.form.DividerRow
import com.aliernfrog.pftool.ui.theme.AppComponentShape
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSheet(
    sheetState: SheetState
) {
    val deviceLanguageCode = androidx.compose.ui.text.intl.Locale.current.let {
        "${it.language}-${it.region}"
    }
    val appLanguageCode = remember { Locale.getDefault().let {
        "${it.language}-${it.country}"
    } }
    val deviceLanguage = languages.find { it.fullCode == deviceLanguageCode }

    @Composable
    fun LanguageButton(language: Language) {
        val isDeviceLanguage = language.fullCode == deviceLanguageCode
        ButtonRow(
            title = if (isDeviceLanguage) stringResource(R.string.settings_appearance_language_device) else language.label,
            description = if (isDeviceLanguage) language.label else language.fullCode,
            painter = rememberVectorPainter(
                if (isDeviceLanguage) Icons.Default.PhoneAndroid else Icons.Default.Translate
            ),
            trailingComponent = if (language.fullCode == appLanguageCode) { {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.CheckCircle),
                    contentDescription = stringResource(R.string.settings_appearance_language_selected)
                )
            } } else null
        ) {
            /* TODO */
        }
    }

    BaseModalBottomSheet(
        sheetState = sheetState,
        dragHandle = { SmallDragHandle() }
    ) { bottomPadding ->
        Text(
            text = stringResource(R.string.settings_appearance_language_select),
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
        )
        DividerRow(
            alpha = 0.3f
        )
        LazyColumn {
            item {
                TranslationHelp(isDeviceLanguageAvailable = deviceLanguage != null)
            }

            if (deviceLanguage != null) item {
                LanguageButton(deviceLanguage)
            }

            items(languages.filter { it != deviceLanguage }) {
                LanguageButton(it)
            }

            item {
                Spacer(Modifier.height(bottomPadding))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationHelp(
    isDeviceLanguageAvailable: Boolean
) {
    val uriHandler = LocalUriHandler.current
    Card(
        shape = AppComponentShape,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        onClick = { uriHandler.openUri(crowdinURL) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.Handshake),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(
                        if (isDeviceLanguageAvailable) R.string.settings_appearance_language_help
                        else R.string.settings_appearance_language_help_deviceNotAvailable
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(stringResource(R.string.settings_appearance_language_help_description))
        }
    }
}