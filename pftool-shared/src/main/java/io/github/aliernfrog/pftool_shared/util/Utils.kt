package io.github.aliernfrog.pftool_shared.util

import android.os.Build
import android.os.Environment

val externalStorageRoot = Environment.getExternalStorageDirectory().toString()+"/"
val hasAndroidDataRestrictions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
val folderPickerSupportsInitialUri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

val supportsPerAppLanguagePreferences = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
