package io.github.aliernfrog.pftool_shared.di

import io.github.aliernfrog.pftool_shared.impl.ProgressState
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val pfToolSharedModule = module {
    singleOf(::ProgressState)
}