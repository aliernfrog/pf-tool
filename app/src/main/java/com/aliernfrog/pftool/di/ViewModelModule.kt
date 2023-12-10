package com.aliernfrog.pftool.di

import com.aliernfrog.pftool.ui.viewmodel.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val viewModelModule = module {
    singleOf(::MainViewModel)
    singleOf(::InsetsViewModel)
    singleOf(::ShizukuViewModel)

    singleOf(::SettingsViewModel)
    singleOf(::MapsViewModel)
    singleOf(::MapsListViewModel)
}