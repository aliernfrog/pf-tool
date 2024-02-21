package com.aliernfrog.pftool.util.extension

import android.os.Environment
import com.aliernfrog.pftool.data.PermissionData

val PermissionData.requiresAndroidData: Boolean
    get() = forceRecommendedPath && recommendedPath?.startsWith(
        "${Environment.getExternalStorageDirectory()}/Android/data"
    ) == true