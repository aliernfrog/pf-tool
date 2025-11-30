package com.aliernfrog.pftool.util.extension

import com.aliernfrog.pftool.impl.FileWrapper
import io.github.aliernfrog.pftool_shared.enum.ListSorting

fun ListSorting.comparator(): Comparator<FileWrapper> {
    return when (this) {
        ListSorting.ALPHABETICAL -> compareBy(FileWrapper::name)
        ListSorting.DATE -> compareByDescending(FileWrapper::lastModified)
        ListSorting.SIZE -> compareBy(FileWrapper::size)
    }
}