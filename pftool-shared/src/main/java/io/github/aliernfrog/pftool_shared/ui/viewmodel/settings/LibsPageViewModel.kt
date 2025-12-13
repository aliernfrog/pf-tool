package io.github.aliernfrog.pftool_shared.ui.viewmodel.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withContext

class LibsPageViewModel(context: Context) : ViewModel() {
    val libraries = Libs.Builder().withContext(context).build().libraries
}