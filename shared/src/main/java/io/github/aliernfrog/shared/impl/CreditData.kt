package io.github.aliernfrog.shared.impl

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.aliernfrog.shared.util.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class CreditData(
    val userName: String,
    val displayNameOverride: Any? = null,
    val description: Any?,
    val fetchFromGithub: Boolean,
    val link: String? = if (fetchFromGithub) "https://github.com/$userName" else null
) {
    private var fetchedOnce = false

    var avatarURL by mutableStateOf<String?>(null)
    var displayName by mutableStateOf(displayNameOverride ?: userName)
    var fetching by mutableStateOf(false)

    suspend fun fetchDetails() {
        if (!fetchFromGithub || fetchedOnce) return
        fetchedOnce = true

        fetching = true
        withContext(Dispatchers.IO) {
            try {
                val res = URL("https://api.github.com/users/$userName").readText()
                val json = JSONObject(res)
                if (displayNameOverride == null) displayName = json.getString("name")
                avatarURL = json.getString("avatar_url")
            } catch (e: Exception) {
                Log.e(TAG, "CreditData/fetchDetails: failed to fetch details of user $userName", e)
            }
        }
        fetching = false
    }
}