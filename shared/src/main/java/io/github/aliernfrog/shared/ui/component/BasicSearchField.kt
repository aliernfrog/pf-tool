package io.github.aliernfrog.shared.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BasicSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    color: Color = SearchBarDefaults.collapsedContainedSearchBarColor
) {
    val focusRequester = remember { FocusRequester() }

    CompositionLocalProvider(LocalContentColor provides contentColorFor(color)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clip(SearchBarDefaults.inputFieldShape)
                .background(color)
                .heightIn(min = 56.dp)
                .clickable {
                    focusRequester.requestFocus()
                }
        ) {
            Spacer(Modifier.width(16.dp))

            leadingIcon?.let {
                it()
                Spacer(Modifier.width(12.dp))
            }

            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                placeholder?.let {
                    if (value.isEmpty()) CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        it()
                    }
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = LocalTextStyle.current,
                    singleLine = singleLine,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }

            trailingIcon?.let {
                it()
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}