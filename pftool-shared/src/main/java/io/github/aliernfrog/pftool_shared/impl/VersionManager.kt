package io.github.aliernfrog.pftool_shared.impl

import android.content.Context
import android.os.Build
import android.util.Log
import io.github.aliernfrog.pftool_shared.data.ReleaseInfo
import io.github.aliernfrog.pftool_shared.util.SharedString
import io.github.aliernfrog.pftool_shared.util.getSharedString
import io.github.aliernfrog.pftool_shared.util.manager.base.BasePreferenceManager
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.net.URL

class VersionManager(
    private val tag: String,
    private val appName: String,
    private val updatesURLPref: BasePreferenceManager.Preference<String>,
    defaultInstallURL: String,
    buildCommit: String,
    buildBranch: String,
    buildHasLocalChanges: Boolean,
    context: Context
) {
    val currentVersionName = "v" + PFToolSharedUtil.getAppVersionName(context)
    val currentVersionCode = PFToolSharedUtil.getAppVersionCode(context)
    val isCurrentlyUsingPreRelease = currentVersionName.contains("-alpha")

    private val _latestVersionInfo = MutableStateFlow(ReleaseInfo(
        versionName = currentVersionName,
        preRelease = isCurrentlyUsingPreRelease,
        body = context.getSharedString(SharedString.UPDATES_NO_CHANGELOG),
        htmlUrl = defaultInstallURL,
        downloadLink = defaultInstallURL
    ))
    val latestVersionInfo: StateFlow<ReleaseInfo> = _latestVersionInfo.asStateFlow()

    private val _updateAvailable = MutableStateFlow(false)
    val updateAvailable: StateFlow<Boolean> = _updateAvailable.asStateFlow()

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
        "$appName $currentVersionName",
        "Android API ${Build.VERSION.SDK_INT}",
        debugInfoPrefs.joinToString("\n") {
            "${it.key}: ${it.value}"
        }
    ).joinToString("\n")

    fun checkUpdates(skipVersionCheck: Boolean = false): UpdateCheckResult {
        return try {
            val responseJSON = JSONObject(URL(updatesURLPref.value).readText())
            val infoJSON = responseJSON.getJSONObject(
                if (isCurrentlyUsingPreRelease && responseJSON.has("preRelease")) "preRelease" else "stable"
            )
            val latestVersionCode = infoJSON.getInt("versionCode")
            _latestVersionInfo.value = ReleaseInfo(
                versionName = infoJSON.getString("versionName"),
                preRelease = infoJSON.getBoolean("preRelease"),
                body = infoJSON.getString(
                    if (infoJSON.has("bodyMarkdown")) "bodyMarkdown" else "body"
                ),
                htmlUrl = infoJSON.getString("htmlUrl"),
                downloadLink = infoJSON.getString("downloadUrl")
            )
            _updateAvailable.value = skipVersionCheck || latestVersionCode > currentVersionCode
            if (!_updateAvailable.value) return UpdateCheckResult.NoUpdates
            UpdateCheckResult.UpdateAvailable(_latestVersionInfo.value)
        } catch (_: CancellationException) {
            UpdateCheckResult.NoUpdates // ignore this exception
        } catch (e: Exception) {
            Log.e(tag, "[VersionManager:checkUpdates] Failed to check for updates", e)
            UpdateCheckResult.Error
        }
    }
}

sealed class UpdateCheckResult {
    data object NoUpdates : UpdateCheckResult()
    data object Error : UpdateCheckResult()
    data class UpdateAvailable(val info: ReleaseInfo) : UpdateCheckResult()
}