package com.aliernfrog.pftool.data

data class ReleaseInfo(
    val versionName: String,
    val preRelease: Boolean,
    val body: String,
    val htmlUrl: String,
    val downloadLink: String
)
