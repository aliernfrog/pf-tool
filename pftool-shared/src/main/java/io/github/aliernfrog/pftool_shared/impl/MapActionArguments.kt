package io.github.aliernfrog.pftool_shared.impl

class EmptyMapActionArguments : IMapActionArguments {
    override val mapName: String? = null
}

interface IMapActionArguments {
    val mapName: String?

    fun resolveMapName(fallback: String): String {
        val str = mapName?.ifBlank { fallback } ?: fallback
        return str.replace("\n", "")
    }
}