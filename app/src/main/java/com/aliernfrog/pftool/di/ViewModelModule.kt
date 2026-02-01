package com.aliernfrog.pftool.di

import com.aliernfrog.pftool.ui.viewmodel.*
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MainViewModel)

    viewModelOf(::SettingsViewModel)
    viewModelOf(::MapsViewModel)
    viewModelOf(::MapsListViewModel)
}