package com.aliernfrog.pftool.util.extension

import com.aliernfrog.pftool.util.staticutil.FileUtil
import com.lazygeniouz.dfc.file.DocumentFileCompat

val DocumentFileCompat.nameWithoutExtension
    get() = FileUtil.removeExtension(this.name)