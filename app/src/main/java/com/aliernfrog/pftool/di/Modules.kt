package com.aliernfrog.pftool.di

import io.github.aliernfrog.pftool_shared.di.pfToolSharedModule
import io.github.aliernfrog.shared.di.sharedModule

val appModules = listOf(
    appModule,
    viewModelModule,
    sharedModule,
    pfToolSharedModule
)