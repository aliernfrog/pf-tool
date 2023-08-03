package com.aliernfrog.pftool.state

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Update
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import com.aliernfrog.pftool.ConfigKey
import com.aliernfrog.pftool.R
import com.aliernfrog.pftool.data.ReleaseInfo
import com.aliernfrog.pftool.githubRepoURL
import com.aliernfrog.pftool.util.staticutil.GeneralUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

@OptIn(ExperimentalMaterialApi::class)
class UpdateState(
    private val topToastState: TopToastState,
    config: SharedPreferences,
    context: Context
) {
    lateinit var scope: CoroutineScope
    val updateSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden, Density(context))

    private val releaseUrl = config.getString(ConfigKey.KEY_APP_UPDATES_URL, ConfigKey.DEFAULT_UPDATES_URL)!!
    private val autoUpdatesEnabled = config.getBoolean(ConfigKey.KEY_APP_AUTO_UPDATES, true)
    private val currentVersionName = GeneralUtil.getAppVersionName(context)
    private val currentVersionCode = GeneralUtil.getAppVersionCode(context)
    private val isCurrentPreRelease = GeneralUtil.getAppVersionName(context).contains("-alpha")

    var latestVersionInfo by mutableStateOf(ReleaseInfo(
        versionName = currentVersionName,
        preRelease = isCurrentPreRelease,
        body = context.getString(R.string.updates_noUpdates),
        htmlUrl = githubRepoURL,
        downloadLink = githubRepoURL
    ))
        private set

    init {
        if (autoUpdatesEnabled) CoroutineScope(Dispatchers.Main).launch {
            checkUpdates()
        }
    }

    suspend fun checkUpdates(
        manuallyTriggered: Boolean = false,
        ignoreVersion: Boolean = false
    ) {
        withContext(Dispatchers.IO) {
            try {
                val responseJson = JSONObject(URL(releaseUrl).readText())
                val branchKey = if (isCurrentPreRelease && responseJson.has("preRelease")) "preRelease" else "stable"
                val json = responseJson.getJSONObject(branchKey)
                val latestVersionCode = json.getInt("versionCode")
                val latestVersionName = json.getString("versionName")
                val latestIsPreRelease = json.getBoolean("preRelease")
                val latestBody = if (json.has("bodyMarkdown")) json.getString("bodyMarkdown") else json.getString("body")
                val latestHtmlUrl = json.getString("htmlUrl")
                val latestDownload = json.getString("downloadUrl")
                val isUpToDate = !ignoreVersion && latestVersionCode <= currentVersionCode
                if (!isUpToDate) {
                    latestVersionInfo = ReleaseInfo(
                        versionName = latestVersionName,
                        preRelease = latestIsPreRelease,
                        body = latestBody,
                        htmlUrl = latestHtmlUrl,
                        downloadLink = latestDownload
                    )
                    if (!manuallyTriggered) showUpdateToast()
                    else coroutineScope {
                        updateSheetState.show()
                    }
                } else {
                    if (manuallyTriggered) topToastState.showToast(
                        text = R.string.updates_noUpdates,
                        icon = Icons.Rounded.Info,
                        iconTintColor = TopToastColor.ON_SURFACE
                    )
                }
            } catch (e: CancellationException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                if (manuallyTriggered) topToastState.showToast(
                    text = R.string.updates_error,
                    icon = Icons.Rounded.PriorityHigh,
                    iconTintColor = TopToastColor.ERROR
                )
            }
        }
    }

    fun showUpdateToast() {
        topToastState.showToast(
            text = R.string.updates_updateAvailable,
            icon = Icons.Rounded.Update,
            stayMs = 20000,
            dismissOnClick = true,
            onToastClick = {
                scope.launch { updateSheetState.show() }
            }
        )
    }
}