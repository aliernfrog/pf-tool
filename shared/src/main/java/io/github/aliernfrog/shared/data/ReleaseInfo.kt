package io.github.aliernfrog.shared.data

import org.json.JSONObject

data class ReleaseInfo(
    val versionName: String,
    val versionCode: Long,
    val prerelease: Boolean,
    val minSdk: Int,
    val body: String?,
    val createdAt: Long,
    val htmlUrl: String,
    val downloadUrl: String
) {
    companion object {
        fun fromJSON(json: JSONObject): ReleaseInfo = ReleaseInfo(
            versionName = json.getString("versionName"),
            versionCode = json.getLong("versionCode"),
            prerelease = json.getBoolean("prerelease"),
            minSdk = json.getInt("minSdk"),
            body = json.getString("body"),
            createdAt = json.getLong("createdAt"),
            htmlUrl = json.getString("htmlUrl"),
            downloadUrl = json.getString("downloadUrl")
        )
    }
}
