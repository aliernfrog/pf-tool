package com.aliernfrog.pftool.ui.component.screen.permissions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.ui.component.ButtonIcon
import com.aliernfrog.pftool.ui.component.CardWithActions
import com.aliernfrog.pftool.ui.component.form.DividerRow
import com.aliernfrog.pftool.ui.viewmodel.ShizukuViewModel
import org.koin.androidx.compose.getViewModel
import rikka.shizuku.Shizuku

@Composable
fun ShizukuWarning(
    shizukuViewModel: ShizukuViewModel = getViewModel(),
    onClickGetStarted: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    Column {
        CardWithActions(
            title = stringResource(R.string.permissions_shizuku_title),
            painter = rememberVectorPainter(Icons.Default.Adb),
            buttons = {
                if (!shizukuViewModel.installed) Button(
                    onClick = {
                        uriHandler.openUri("https://shizuku.rikka.app/download/")
                    }
                ) {
                    ButtonIcon(
                        painter = rememberVectorPainter(Icons.AutoMirrored.Filled.OpenInNew)
                    )
                    Text(stringResource(R.string.permissions_shizuku_download))
                }
                else Button(
                    onClick = {
                        Shizuku.requestPermission(0)
                    }
                ) {
                    Text(stringResource(R.string.permissions_shizuku_grant))
                }
            }
        ) {
            Text(stringResource(R.string.permissions_shizuku_description))
        }

        DividerRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}