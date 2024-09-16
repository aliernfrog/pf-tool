package com.aliernfrog.pftool.util

import com.aliernfrog.pftool.data.ServiceFile
import com.aliernfrog.pftool.util.extension.size
import java.io.File

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