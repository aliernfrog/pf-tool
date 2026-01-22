package io.github.aliernfrog.shared.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSwitchRow
import io.github.aliernfrog.shared.ui.theme.AppComponentShape
import io.github.aliernfrog.shared.ui.viewmodel.settings.ExperimentalPageViewModel
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import io.github.aliernfrog.shared.util.sharedStringResource
import io.github.aliernfrog.shared.util.showUpdateToast
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExperimentalPage(
    vm: ExperimentalPageViewModel = koinViewModel(),
    experimentalPrefs: List<BasePreferenceManager.Preference<*>>,
    experimentalOptionsEnabledPref: BasePreferenceManager.Preference<Boolean>,
    onCheckUpdatesRequest: (skipVersionCheck: Boolean) -> Unit,
    onNavigateUpdatesScreenRequest: () -> Unit,
    onRestartAppRequest: () -> Unit,
    onNavigateBackRequest: () -> Unit,
    extraContent: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    val sortedExperimentalOptions = remember {
        experimentalPrefs.sortedBy {
            when (it.defaultValue) {
                is Boolean -> 0
                is String -> 1
                is Int -> 2
                is Long -> 3
                else -> 99
            }
        }
    }

    SettingsPageContainer(
        title = sharedStringResource(SharedString.SettingsExperimental),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        ExpressiveSwitchRow(
            title = sharedStringResource(SharedString.SettingsExperimental),
            description = sharedStringResource(SharedString.SettingsExperimentalDescription),
            checked = experimentalOptionsEnabledPref.value,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 12.dp)
                .clip(AppComponentShape),
            onCheckedChange = { experimentalOptionsEnabledPref.value = it }
        )

        extraContent()

        ExpressiveSection(title = "Updates") {
            VerticalSegmentor(
                {
                    ExpressiveButtonRow(title = "Check updates (skip version check)") {
                        onCheckUpdatesRequest(/* skipVersionCheck */ true)
                    }
                }, {
                    ExpressiveButtonRow(title = "Show update toast") {
                        showUpdateToast { onNavigateUpdatesScreenRequest() }
                    }
                }, {
                    ExpressiveButtonRow(title = "Show update screen") {
                        onNavigateUpdatesScreenRequest()
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        ExpressiveSection(title = "Prefs") {
            val inputs: List<@Composable () -> Unit> = sortedExperimentalOptions.map { pref ->
                {
                    @Composable
                    fun TextField(onValueChange: (String) -> Unit, isNumberOnly: Boolean = false) {
                        OutlinedTextField(
                            value = pref.value.toString(),
                            onValueChange = onValueChange,
                            label = { Text(pref.key) },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = if (isNumberOnly) KeyboardType.Number else KeyboardType.Unspecified
                            ),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { pref.resetValue() },
                            shapes = IconButtonDefaults.shapes(),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Restore, contentDescription = "Reset")
                        }

                        when (pref.defaultValue) {
                            is Boolean -> {
                                pref as BasePreferenceManager.Preference<Boolean>
                                ExpressiveSwitchRow(title = pref.key, checked = pref.value) {
                                    pref.value = it
                                }
                            }
                            is String -> {
                                pref as BasePreferenceManager.Preference<String>
                                TextField(
                                    onValueChange = { pref.value = it },
                                )
                            }
                            is Int -> {
                                pref as BasePreferenceManager.Preference<Int>
                                TextField(
                                    onValueChange = {
                                        pref.value = it.toIntOrNull() ?: pref.defaultValue
                                    },
                                    isNumberOnly = true
                                )
                            }
                            is Long -> {
                                pref as BasePreferenceManager.Preference<Long>
                                TextField(
                                    onValueChange = {
                                        pref.value = it.toLongOrNull() ?: pref.defaultValue
                                    },
                                    isNumberOnly = true
                                )
                            }
                        }
                    }
                }
            }

            VerticalSegmentor(
                *inputs.toTypedArray(),
                {
                    ExpressiveButtonRow(
                        title = "Reset experimental prefs",
                        contentColor = MaterialTheme.colorScheme.error
                    ) {
                        scope.launch {
                            sortedExperimentalOptions.forEach {
                                it.resetValue()
                            }
                            vm.topToastState.showAndroidToast(
                                text = "Restored default values for experimental prefs",
                                icon = Icons.Rounded.Done
                            )
                            onRestartAppRequest()
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        ExpressiveSection(title = "Crash handler") {
            VerticalSegmentor(
                {
                    ExpressiveButtonRow(
                        title = "Test crash handler",
                        description = "(by crashing)"
                    ) {
                        throw Exception("Did the crash handler test go well?")
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}