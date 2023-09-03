package com.aliernfrog.pftool.util.manager

import android.content.Context

class ContextUtils(
    context: Context
) {
    var getString: (id: Int) -> String
        private set

    init {
        getString = {
            context.getString(it)
        }
    }
}