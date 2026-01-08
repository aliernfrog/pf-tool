package com.aliernfrog.pftool.di

import com.aliernfrog.pftool.BuildConfig
import com.aliernfrog.pftool.util.extension.enable
import com.aliernfrog.pftool.util.manager.PreferenceManager
import io.github.aliernfrog.pftool_shared.di.getPFToolSharedModule
import io.github.aliernfrog.shared.di.getKoinInstance
import io.github.aliernfrog.shared.di.sharedModule

val appModules = listOf(
    appModule,
    viewModelModule,
    sharedModule,
    getPFToolSharedModule(
        applicationId = BuildConfig.APPLICATION_ID,
        isDebugBuild = BuildConfig.DEBUG,
        shizukuNeverLoadPref = {
            getKoinInstance<PreferenceManager>().shizukuNeverLoad
        },
        storageAccessTypePref = {
            getKoinInstance<PreferenceManager>().storageAccessType
        },
        ignoreDocumentsUIRestrictionsPref = {
            getKoinInstance<PreferenceManager>().ignoreDocumentsUIRestrictions
        },
        onSetStorageAccessType = {
            it.enable()
        }
    )
)