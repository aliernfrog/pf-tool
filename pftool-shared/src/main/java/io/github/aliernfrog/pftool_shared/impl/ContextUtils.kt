package io.github.aliernfrog.pftool_shared.impl

import android.content.Context

class ContextUtils(
    context: Context
) {
    var run: (block: (Context) -> Unit) -> Unit
        private set

    var getString: (id: Int) -> String
        private set

    var stringFunction: (block: (Context) -> String) -> String
        private set

    init {
        run = {
            it(context)
        }

        getString = {
            context.getString(it)
        }

        stringFunction = {
            it(context)
        }
    }
}