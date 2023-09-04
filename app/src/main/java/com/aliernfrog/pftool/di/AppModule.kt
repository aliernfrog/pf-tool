package com.aliernfrog.pftool.di

import com.aliernfrog.pftool.util.manager.ContextUtils
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::ContextUtils)
    singleOf(::PreferenceManager)
    single {
        TopToastState(composeView = null)
    }
}