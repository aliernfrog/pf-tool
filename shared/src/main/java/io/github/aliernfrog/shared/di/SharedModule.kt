package io.github.aliernfrog.shared.di

import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.domain.IAppState
import io.github.aliernfrog.shared.impl.InsetsManager
import io.github.aliernfrog.shared.impl.ContextUtils
import io.github.aliernfrog.shared.ui.viewmodel.settings.AboutPageViewModel
import io.github.aliernfrog.shared.ui.viewmodel.settings.ExperimentalPageViewModel
import io.github.aliernfrog.shared.ui.viewmodel.settings.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun getSharedModule(
    sharedString: SharedString
): Module = module {
    single { sharedString }
    singleOf(::IAppState)
    singleOf(::InsetsManager)
    singleOf(::ContextUtils)

    viewModelOf(::AboutPageViewModel)
    viewModelOf(::ExperimentalPageViewModel)
    viewModelOf(::SettingsViewModel)
}