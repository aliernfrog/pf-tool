package io.github.aliernfrog.shared.di

import io.github.aliernfrog.shared.impl.InsetsManager
import io.github.aliernfrog.shared.impl.ContextUtils
import io.github.aliernfrog.shared.ui.viewmodel.settings.AboutPageViewModel
import io.github.aliernfrog.shared.ui.viewmodel.settings.ExperimentalPageViewModel
import io.github.aliernfrog.shared.ui.viewmodel.settings.LibsPageViewModel
import io.github.aliernfrog.shared.ui.viewmodel.settings.SettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {
    viewModelOf(::AboutPageViewModel)
    viewModelOf(::ExperimentalPageViewModel)
    viewModelOf(::LibsPageViewModel)
    viewModelOf(::SettingsViewModel)

    singleOf(::InsetsManager)
    singleOf(::ContextUtils)
}