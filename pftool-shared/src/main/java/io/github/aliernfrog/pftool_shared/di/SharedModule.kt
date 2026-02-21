package io.github.aliernfrog.pftool_shared.di

import io.github.aliernfrog.pftool_shared.enum.StorageAccessType
import io.github.aliernfrog.pftool_shared.impl.FileWrapper
import io.github.aliernfrog.pftool_shared.impl.IMapFile
import io.github.aliernfrog.pftool_shared.impl.LocaleManager
import io.github.aliernfrog.pftool_shared.impl.ProgressState
import io.github.aliernfrog.pftool_shared.impl.ShizukuManager
import io.github.aliernfrog.pftool_shared.repository.FileRepository
import io.github.aliernfrog.pftool_shared.repository.MapFileFinder
import io.github.aliernfrog.pftool_shared.repository.MapRepository
import io.github.aliernfrog.pftool_shared.repository.ServiceFileRepository
import io.github.aliernfrog.pftool_shared.ui.viewmodel.IMapsListViewModel
import io.github.aliernfrog.pftool_shared.ui.viewmodel.IPermissionsViewModel
import io.github.aliernfrog.pftool_shared.util.PFToolSharedString
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun getPFToolSharedModule(
    applicationId: String,
    isDebugBuild: Boolean,
    languageCodes: Array<String>,
    translationProgresses: FloatArray,
    baseLanguageCode: String,
    sharedString: PFToolSharedString,
    importedMapsFinder: MapFileFinder,
    exportedMapsFinder: MapFileFinder,
    getFileAsMapFile: (FileWrapper) -> IMapFile,
    languagePref: () -> BasePreferenceManager.Preference<String>,
    shizukuNeverLoadPref: () -> BasePreferenceManager.Preference<Boolean>,
    storageAccessTypePref: () -> BasePreferenceManager.Preference<Int>,
    ignoreDocumentsUIRestrictionsPref: () -> BasePreferenceManager.Preference<Boolean>,
    onSetStorageAccessType: (StorageAccessType) -> Unit
): Module = module {
    single { sharedString }
    singleOf(::ProgressState)

    single {
        LocaleManager(
            languageCodes = languageCodes,
            translationProgresses = translationProgresses,
            languagePref = languagePref,
            baseLanguageCode = baseLanguageCode,
            context = get()
        )
    }

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

    single {
        FileRepository(
            storageAccessTypePref = storageAccessTypePref,
            serviceFileRepository = get()
        )
    }

    single {
        MapRepository(
            importedMapsFinder = importedMapsFinder,
            exportedMapsFinder = exportedMapsFinder,
            getFileAsMapFile = getFileAsMapFile,
            fileRepository = get()
        )
    }

    viewModelOf(::IMapsListViewModel)
    viewModel {
        IPermissionsViewModel(
            storageAccessTypePref = storageAccessTypePref,
            ignoreDocumentsUIRestrictionsPref = ignoreDocumentsUIRestrictionsPref,
            onSetStorageAccessType = onSetStorageAccessType,
            topToastState = get(),
            shizukuManager = get(),
            context = get()
        )
    }
}