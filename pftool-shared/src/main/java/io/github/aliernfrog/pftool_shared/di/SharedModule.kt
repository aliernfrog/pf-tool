package io.github.aliernfrog.pftool_shared.di

import io.github.aliernfrog.pftool_shared.impl.ProgressState
import io.github.aliernfrog.pftool_shared.impl.ShizukuManager
import io.github.aliernfrog.pftool_shared.repository.ServiceFileRepository
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun getPFToolSharedModule(
    applicationId: String,
    isDebugBuild: Boolean,
    shizukuNeverLoadPref: () -> BasePreferenceManager.Preference<Boolean>,
): Module = module {
    singleOf(::ProgressState)

    single {
        ShizukuManager(
            applicationId = applicationId,
            isDebugBuild = isDebugBuild,
            shizukuNeverLoadPref = shizukuNeverLoadPref,
            topToastState = get(),
            context = get()
        )
    }

    singleOf(::ServiceFileRepository)
}