package com.aliernfrog.pftool.util.extension

import androidx.compose.runtime.snapshots.SnapshotStateList

fun SnapshotStateList<*>.removeLastIfMultiple() {
    if (this.size <= 1) return
    this.removeAt(this.lastIndex)
}