package com.aliernfrog.pftool.di

import com.aliernfrog.pftool.BuildConfig
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.util.extension.enable
import com.aliernfrog.pftool.util.manager.PreferenceManager
import io.github.aliernfrog.pftool_shared.di.getPFToolSharedModule
import io.github.aliernfrog.pftool_shared.repository.MapFileFinder
import io.github.aliernfrog.shared.di.getKoinInstance
import io.github.aliernfrog.shared.di.sharedModule

val appModules = listOf(
    appModule,
    viewModelModule,
    sharedModule,
    getPFToolSharedModule(
        applicationId = BuildConfig.APPLICATION_ID,
        isDebugBuild = BuildConfig.DEBUG,
        languageCodes = BuildConfig.LANGUAGES,
        translationProgresses = BuildConfig.TRANSLATION_PROGRESSES,
        baseLanguageCode = "en-US",
        importedMapsFinder = MapFileFinder(
            pathPref = { getKoinInstance<PreferenceManager>().pfMapsDir },
            isMapFile = { !it.isFile }
        ),
        exportedMapsFinder = MapFileFinder(
            pathPref = { getKoinInstance<PreferenceManager>().exportedMapsDir },
            isMapFile = { it.isFile && it.name.endsWith(".zip", ignoreCase = true) }
        ),
        getFileAsMapFile = { MapFile(it) },
        languagePref = {
            getKoinInstance<PreferenceManager>().language
        },
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