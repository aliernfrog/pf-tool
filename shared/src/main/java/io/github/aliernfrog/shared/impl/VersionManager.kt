package io.github.aliernfrog.shared.impl

import android.content.Context
import android.os.Build
import android.util.Log
import io.github.aliernfrog.shared.data.ReleaseInfo
import io.github.aliernfrog.shared.util.extension.getAppVersionCode
import io.github.aliernfrog.shared.util.extension.getAppVersionName
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

class VersionManager(
    private val tag: String,
    private val appName: String,
    private val releasesURLPref: BasePreferenceManager.Preference<String>,
    defaultInstallURL: String,
    buildCommit: String,
    buildBranch: String,
    buildHasLocalChanges: Boolean,
    context: Context
) {
    val currentVersionName = "v" + context.getAppVersionName()
    val currentVersionCode = context.getAppVersionCode()
    val isCurrentlyUsingPreRelease = currentVersionName.contains("-alpha")

    private val _currentVersionInfo = MutableStateFlow(ReleaseInfo(
        versionName = currentVersionName,
        versionCode = currentVersionCode,
        prerelease = isCurrentlyUsingPreRelease,
        minSdk = Build.VERSION.SDK_INT,
        body = null,
        createdAt = 0,
        htmlUrl = defaultInstallURL,
        downloadUrl = defaultInstallURL
    ))
    val currentVersionInfo = _currentVersionInfo.asStateFlow()

    private val _availableUpdates = MutableStateFlow(listOf<ReleaseInfo>())
    val availableUpdates = _availableUpdates.asStateFlow()

    private val _isCompatibleWithLatestVersion = MutableStateFlow(true)
    val isCompatibleWithLatestVersion = _isCompatibleWithLatestVersion.asStateFlow()

    private val _isCheckingForUpdates = MutableStateFlow(false)
    val isCheckingForUpdates = _isCheckingForUpdates.asStateFlow()

    val versionLabel = "$currentVersionName (${
        buildCommit.ifBlank { currentVersionCode.toString() }
    }${
        if (buildHasLocalChanges) "*" else ""
    }${
        buildBranch.let {
            if (it == currentVersionName) ""
            else " - ${it.ifBlank { "local" }}"
        }
    })"

    fun getDebugInfo(debugInfoPrefs: List<BasePreferenceManager.Preference<*>>): String = arrayOf(
        "$appName $versionLabel",
        "Android API ${Build.VERSION.SDK_INT}",
        debugInfoPrefs.joinToString("\n") {
            "${it.key}: ${it.value}"
        }
    ).joinToString("\n")

    suspend fun checkUpdates(skipVersionCheck: Boolean = false): UpdateCheckResult = withContext(Dispatchers.IO) {
        try {
            _isCheckingForUpdates.value = true
            val releasesJSONArray = JSONArray(URL(releasesURLPref.value).readText())
            val releases = mutableListOf<ReleaseInfo>()
            for (i in 0 until releasesJSONArray.length()) {
                val releaseJSON = releasesJSONArray.getJSONObject(i)
                val release = ReleaseInfo.fromJSON(releaseJSON)
                releases.add(release)
            }
            releases.sortByDescending { it.versionCode }
            val updates = releases.filter { release ->
                skipVersionCheck || (release.versionCode > currentVersionCode
                        && (isCurrentlyUsingPreRelease || !release.prerelease)
                        && Build.VERSION.SDK_INT >= release.minSdk)
            }
            val isCompatibleWithLatest = Build.VERSION.SDK_INT >= releases.first().minSdk
            val currentRelease = releases.find { release ->
                release.versionCode == currentVersionCode
            }
            currentRelease?.let {
                _currentVersionInfo.value = it
            }
            _availableUpdates.value = updates
            _isCompatibleWithLatestVersion.value = isCompatibleWithLatest
            if (updates.isEmpty()) UpdateCheckResult.NoUpdates
            else UpdateCheckResult.UpdatesAvailable(updates)
        } catch (_: CancellationException) {
            UpdateCheckResult.NoUpdates // ignore this exception
        } catch (e: Exception) {
            Log.e(tag, "[VersionManager:checkUpdates] Failed to check for updates", e)
            UpdateCheckResult.Error
        } finally {
            _isCheckingForUpdates.value = false
        }
    }
}

sealed class UpdateCheckResult {
    data object NoUpdates : UpdateCheckResult()
    data object Error : UpdateCheckResult()
    data class UpdatesAvailable(val updates: List<ReleaseInfo>) : UpdateCheckResult()
}