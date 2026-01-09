package io.github.aliernfrog.pftool_shared.impl

class DefaultMapActionArguments(
    override val mapName: String? = null
) : IMapActionArguments

interface IMapActionArguments {
    val mapName: String?

    fun resolveMapName(fallback: String): String {
        val str = mapName?.ifBlank { fallback } ?: fallback
        return str.replace("\n", "")
    }
}