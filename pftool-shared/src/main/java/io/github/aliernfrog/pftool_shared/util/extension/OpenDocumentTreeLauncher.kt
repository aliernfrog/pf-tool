package io.github.aliernfrog.pftool_shared.util.extension

import android.content.ActivityNotFoundException
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import io.github.aliernfrog.shared.util.TAG

fun ManagedActivityResultLauncher<Uri?, Uri?>.launchSafely(
    input: Uri?,
    onNoLauncherException: () -> Unit,
    onUnknownError: (Exception) -> Unit
) {
    try {
        launch(input)
    } catch (e: ActivityNotFoundException) {
        Log.e(TAG, "OpenDocumentTreeLauncher/launchSafely: Failed to launch (ActivityNotFoundException)", e)
        onNoLauncherException()
    } catch (e: Exception) {
        Log.e(TAG, "OpenDocumentTreeLauncher/launchSafely: Failed to launch", e)
        onUnknownError(e)
    }
}