package com.aliernfrog.pftool.ui.composable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aliernfrog.pftool.PFToolComposableShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PFToolTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        textColor = MaterialTheme.colorScheme.onSecondary,
        containerColor = MaterialTheme.colorScheme.secondary,
        cursorColor = MaterialTheme.colorScheme.onSecondary,
        selectionColors = TextSelectionColors(handleColor = MaterialTheme.colorScheme.onSecondary, backgroundColor = MaterialTheme.colorScheme.onSecondary.copy(0.5f)),
        focusedLabelColor = MaterialTheme.colorScheme.onSecondary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSecondary.copy(0.7f),
        placeholderColor = MaterialTheme.colorScheme.onSecondary.copy(0.7f)
    )
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(all = 8.dp).clip(PFToolComposableShape).animateContentSize(),
        label = label,
        placeholder = placeholder,
        singleLine = singleLine,
        colors = colors
    )
}