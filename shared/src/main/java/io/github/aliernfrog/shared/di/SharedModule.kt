package io.github.aliernfrog.shared.di

import io.github.aliernfrog.shared.impl.ContextUtils
import io.github.aliernfrog.shared.ui.viewmodel.InsetsViewModel
import io.github.aliernfrog.shared.ui.viewmodel.settings.AboutPageViewModel
import io.github.aliernfrog.shared.ui.viewmodel.settings.ExperimentalPageViewModel
import io.github.aliernfrog.shared.ui.viewmodel.settings.LibsPageViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    singleOf(::InsetsViewModel)
    singleOf(::AboutPageViewModel)
    singleOf(::ExperimentalPageViewModel)
    singleOf(::LibsPageViewModel)

    singleOf(::ContextUtils)
}