package com.aliernfrog.pftool.ui.composable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.util.GeneralUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PFToolBaseScaffold(title: String, navController: NavController, onNavigationClick: (() -> Unit)? = null, content: @Composable (ColumnScope.() -> Unit)) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).imePadding(),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = LocalContext.current.getString(R.string.action_back), Modifier.padding(horizontal = 8.dp).clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false),
                            onClick = {
                                if (onNavigationClick != null) onNavigationClick()
                                navController.navigateUp()
                            })
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).animateContentSize().padding(padding)) {
            content()
            Spacer(Modifier.height(60.dp+GeneralUtil.getNavigationBarHeight()))
        }
    }
}