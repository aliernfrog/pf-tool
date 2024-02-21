package com.aliernfrog.pftool.util

import com.aliernfrog.pftool.data.ServiceFile
import com.aliernfrog.pftool.util.extension.size
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

inline fun <reified T> getKoinInstance(): T {
    return object : KoinComponent {
        val value: T by inject()
    }.value
}

fun getServiceFile(file: File): ServiceFile {
    return ServiceFile(
        name = file.name,
        path = file.path,
        parentPath = file.parent,
        size = file.size,
        lastModified = file.lastModified(),
        isFile = file.isFile
    )
}