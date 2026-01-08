package io.github.aliernfrog.pftool_shared.di

import io.github.aliernfrog.pftool_shared.enum.StorageAccessType
import io.github.aliernfrog.pftool_shared.impl.ProgressState
import io.github.aliernfrog.pftool_shared.impl.ShizukuManager
import io.github.aliernfrog.pftool_shared.repository.ServiceFileRepository
import io.github.aliernfrog.pftool_shared.ui.viewmodel.PermissionsViewModel
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun getPFToolSharedModule(
    applicationId: String,
    isDebugBuild: Boolean,
    shizukuNeverLoadPref: () -> BasePreferenceManager.Preference<Boolean>,
    storageAccessTypePref: () -> BasePreferenceManager.Preference<Int>,
    ignoreDocumentsUIRestrictionsPref: () -> BasePreferenceManager.Preference<Boolean>,
    onSetStorageAccessType: (StorageAccessType) -> Unit
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

    single {
        PermissionsViewModel(
            storageAccessTypePref = storageAccessTypePref,
            ignoreDocumentsUIRestrictionsPref = ignoreDocumentsUIRestrictionsPref,
            onSetStorageAccessType = onSetStorageAccessType,
            topToastState = get(),
            shizukuManager = get(),
            context = get()
        )
    }

    singleOf(::ServiceFileRepository)
}