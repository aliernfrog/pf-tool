package com.aliernfrog.pftool.di

import com.aliernfrog.pftool.BuildConfig
import com.aliernfrog.pftool.TAG
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.shared.impl.VersionManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::PreferenceManager)

    single {
        get<PreferenceManager>().let { prefs ->
            @Suppress("KotlinConstantConditions") VersionManager(
                tag = TAG,
                appName = "PF Tool",
                updatesURLPref = prefs.updatesURL,
                defaultInstallURL = "https://github.com/aliernfrog/pf-tool",
                buildCommit = BuildConfig.GIT_COMMIT,
                buildBranch = BuildConfig.GIT_BRANCH,
                buildHasLocalChanges = BuildConfig.GIT_LOCAL_CHANGES,
                context = get()
            )
        }
    }

    single {
        TopToastState(
            composeView = null,
            appTheme = null,
            allowSwipingByDefault = false
        )
    }
}