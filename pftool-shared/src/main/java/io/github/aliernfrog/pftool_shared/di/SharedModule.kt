package io.github.aliernfrog.pftool_shared.di

import io.github.aliernfrog.pftool_shared.impl.ContextUtils
import io.github.aliernfrog.pftool_shared.impl.ProgressState
import io.github.aliernfrog.pftool_shared.ui.viewmodel.InsetsViewModel
import io.github.aliernfrog.pftool_shared.ui.viewmodel.settings.AboutPageViewModel
import io.github.aliernfrog.pftool_shared.ui.viewmodel.settings.ExperimentalPageViewModel
import io.github.aliernfrog.pftool_shared.ui.viewmodel.settings.LibsPageViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val pfToolSharedModule = module {
    singleOf(::InsetsViewModel)
    singleOf(::AboutPageViewModel)
    singleOf(::ExperimentalPageViewModel)
    singleOf(::LibsPageViewModel)

    singleOf(::ContextUtils)
    singleOf(::ProgressState)
}