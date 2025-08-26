package com.aliernfrog.pftool.impl

class MapActionArguments(
    private val mapName: String? = null
) {
    fun resolveMapName(fallback: String): String {
        val str = mapName?.ifBlank { fallback } ?: fallback
        return str.replace("\n", "")
    }
}