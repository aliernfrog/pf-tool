package com.aliernfrog.pftool.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.unit.Density
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.pftool.BuildConfig
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.TAG
import com.aliernfrog.pftool.data.Language
import com.aliernfrog.pftool.data.MediaViewData
import com.aliernfrog.pftool.data.ReleaseInfo
import com.aliernfrog.pftool.di.get
import com.aliernfrog.pftool.enum.MapsListSegment
import com.aliernfrog.pftool.githubRepoURL
import com.aliernfrog.pftool.impl.MapFile
import com.aliernfrog.pftool.impl.Progress
import com.aliernfrog.pftool.impl.ProgressState
import com.aliernfrog.pftool.supportsPerAppLanguagePreferences
import com.aliernfrog.pftool.util.Destination
import com.aliernfrog.pftool.util.extension.cacheFile
import com.aliernfrog.pftool.util.extension.getAvailableLanguage
import com.aliernfrog.pftool.util.extension.showErrorToast
import com.aliernfrog.pftool.util.extension.toLanguage
import com.aliernfrog.pftool.util.manager.PreferenceManager
import com.aliernfrog.pftool.util.staticutil.GeneralUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
class MainViewModel(
    context: Context,
    val prefs: PreferenceManager,
    val topToastState: TopToastState,
    val progressState: ProgressState
) : ViewModel() {
    lateinit var scope: CoroutineScope
    val updateSheetState = SheetState(skipPartiallyExpanded = false, Density(context))

    private val applicationVersionName = "v${GeneralUtil.getAppVersionName(context)}"
    private val applicationVersionCode = GeneralUtil.getAppVersionCode(context)
    private val applicationIsPreRelease = applicationVersionName.contains("-alpha")
    val applicationVersionLabel = "$applicationVersionName (${
        BuildConfig.GIT_COMMIT.ifBlank { applicationVersionCode.toString() }
    }${
        if (BuildConfig.GIT_LOCAL_CHANGES) "*" else ""
    }${
        BuildConfig.GIT_BRANCH.let {
            if (it == applicationVersionName) ""
            else " - ${it.ifBlank { "local" }}"
        }
    })"

    private val defaultLanguage = GeneralUtil.getLanguageFromCode("en-US")!!
    val deviceLanguage = LocaleManagerCompat.getSystemLocales(context)[0]?.toLanguage() ?: defaultLanguage

    private var _appLanguage by mutableStateOf<Language?>(null)
    var appLanguage: Language?
        get() = _appLanguage ?: deviceLanguage.getAvailableLanguage() ?: defaultLanguage
        set(language) {
            prefs.language.value = language?.fullCode ?: ""
            val localeListCompat = if (language == null) LocaleListCompat.getEmptyLocaleList()
            else LocaleListCompat.forLanguageTags(language.languageCode)
            AppCompatDelegate.setApplicationLocales(localeListCompat)
            _appLanguage = language?.getAvailableLanguage()
        }

    var latestVersionInfo by mutableStateOf(ReleaseInfo(
        versionName = applicationVersionName,
        preRelease = applicationIsPreRelease,
        body = context.getString(R.string.updates_noChangelog),
        htmlUrl = githubRepoURL,
        downloadLink = githubRepoURL
    ))
        private set

    var updateAvailable by mutableStateOf(false)
        private set

    val debugInfo: String
        get() = arrayOf(
            "PF Tool $applicationVersionLabel",
            "Android API ${Build.VERSION.SDK_INT}",
            prefs.debugInfoPrefs.joinToString("\n") {
                "${it.key}: ${it.value}"
            }
        ).joinToString("\n")

    var mediaViewData by mutableStateOf<MediaViewData?>(null)
        private set

    init {
        if (!supportsPerAppLanguagePreferences && prefs.language.value.isNotBlank()) runBlocking {
            appLanguage = GeneralUtil.getLanguageFromCode(prefs.language.value)?.getAvailableLanguage()
        }
        prefs.lastKnownInstalledVersion.value = applicationVersionCode
    }

    suspend fun checkUpdates(
        manuallyTriggered: Boolean = false,
        ignoreVersion: Boolean = false
    ) {
        withContext(Dispatchers.IO) {
            try {
                val updatesURL = prefs.updatesURL.value
                val responseJson = JSONObject(URL(updatesURL).readText())
                val json = responseJson.getJSONObject(
                    if (applicationIsPreRelease && responseJson.has("preRelease")) "preRelease" else "stable"
                )
                val latestVersionCode = json.getInt("versionCode")
                latestVersionInfo = ReleaseInfo(
                    versionName = json.getString("versionName"),
                    preRelease = json.getBoolean("preRelease"),
                    body = json.getString(
                        if (json.has("bodyMarkdown")) "bodyMarkdown" else "body"
                    ),
                    htmlUrl = json.getString("htmlUrl"),
                    downloadLink = json.getString("downloadUrl")
                )
                updateAvailable = ignoreVersion || latestVersionCode > applicationVersionCode
                if (updateAvailable) {
                    if (manuallyTriggered) coroutineScope {
                        updateSheetState.show()
                    } else {
                        showUpdateToast()
                        Destination.SETTINGS.hasNotification.value = true
                    }
                } else {
                    if (manuallyTriggered) withContext(Dispatchers.Main) {
                        topToastState.showAndroidToast(
                            text = R.string.updates_noUpdates,
                            icon = Icons.Rounded.Info,
                            iconTintColor = TopToastColor.ON_SURFACE
                        )
                    }
                }
            } catch (e: CancellationException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                if (manuallyTriggered) withContext(Dispatchers.Main) {
                    topToastState.showAndroidToast(
                        text = R.string.updates_error,
                        icon = Icons.Rounded.PriorityHigh,
                        iconTintColor = TopToastColor.ERROR
                    )
                }
            }
        }
    }

    fun showUpdateToast() {
        topToastState.showToast(
            text = R.string.updates_updateAvailable,
            icon = Icons.Rounded.Update,
            duration = 20000,
            dismissOnClick = true,
            swipeToDismiss = true,
            onToastClick = {
                scope.launch { updateSheetState.show() }
            }
        )
    }

    fun showMediaView(data: MediaViewData) {
        mediaViewData = data
    }

    fun dismissMediaView() {
        mediaViewData = null
    }

    fun handleIntent(intent: Intent, context: Context) {
        val mapsViewModel = get<MapsViewModel>()
        val mapsListViewModel = get<MapsListViewModel>()

        try {
            val uris: MutableList<Uri> = intent.data?.let {
                mutableListOf(it)
            } ?: mutableListOf()
            intent.clipData?.let { clipData ->
                for (i in 0..<clipData.itemCount) {
                    uris.add(clipData.getItemAt(i).uri)
                }
            }
            if (uris.isEmpty()) return

            progressState.currentProgress = Progress(context.getString(R.string.info_pleaseWait))
            viewModelScope.launch(Dispatchers.IO) {
                val cached = uris.map { uri ->
                    MapFile(uri.cacheFile(context)!!)
                }
                if (cached.size <= 1) {
                    mapsViewModel.chooseMap(cached.first())
                    mapsViewModel.mapListShown = false
                } else {
                    mapsViewModel.sharedMaps = cached.toMutableStateList()
                    mapsListViewModel.chosenSegment = MapsListSegment.SHARED
                }
                progressState.currentProgress = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "handleIntent: $e")
            topToastState.showErrorToast()
            progressState.currentProgress = null
        }
    }
}