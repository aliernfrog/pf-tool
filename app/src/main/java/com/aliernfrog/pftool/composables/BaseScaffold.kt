package com.aliernfrog.pftool.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aliernfrog.pftool.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BaseScaffold(title: String, navController: NavController, onNavigationClick: (() -> Unit)? = null, content: @Composable (ColumnScope.() -> Unit)) {
    Scaffold(
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopAppBar(backgroundColor = MaterialTheme.colors.secondary,
                contentPadding = PaddingValues(horizontal = 24.dp),
                content = {
                    if (navController.previousBackStackEntry != null) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = LocalContext.current.getString(R.string.action_back),
                            Modifier.padding(end = 24.dp).clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = false),
                                onClick = {
                                    if (onNavigationClick != null) onNavigationClick()
                                    navController.navigateUp()
                                }
                            )
                        )
                    }
                    Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onSecondary)
                })
        },
        content = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())) {
                Column(Modifier.padding(horizontal = 24.dp), content = content)
            }
        }
    )
}