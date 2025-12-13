package com.aliernfrog.pftool.di

import io.github.aliernfrog.pftool_shared.di.pfToolSharedModule

val appModules = listOf(
    appModule,
    viewModelModule,
    pfToolSharedModule
)