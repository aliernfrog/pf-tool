package io.github.aliernfrog.pftool_shared.util.manager.base

import android.content.SharedPreferences
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import io.github.aliernfrog.pftool_shared.enum.ListSorting
import io.github.aliernfrog.pftool_shared.enum.ListStyle
import io.github.aliernfrog.shared.util.manager.BasePreferenceManager

open class PFToolBasePreferenceManager(prefs: SharedPreferences) : BasePreferenceManager(prefs) {
    data class WindowSizeClassValueGroup<T>(
        val compact: T,
        val medium: T,
        val expanded: T
    ) {
        @Suppress("unused")
        @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
        @Composable
        fun getCurrent(): T {
            val windowSizeClass = calculateWindowSizeClass(LocalActivity.current!!)
            return when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> compact
                WindowWidthSizeClass.Medium -> medium
                WindowWidthSizeClass.Expanded -> expanded
                else -> compact
            }
        }
    }

    class WindowSizeClassPreferenceGroup<T>(
        val key: String,
        val defaultValues: WindowSizeClassValueGroup<T>,
        private val getter: (key: String, defaultValue: T) -> T,
        private val setter: (key: String, newValue: T) -> Unit
    ) {
        private fun createSubPreference(suffix: String, defaultValue: T): Preference<T> = Preference(
            key = "$key.$suffix",
            defaultValue = defaultValue,
            getter, setter
        )

        val compact = createSubPreference("compact", defaultValues.compact)
        val medium = createSubPreference("medium", defaultValues.medium)
        val expanded = createSubPreference("expanded", defaultValues.expanded)

        @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
        @Composable
        fun getCurrent(): Preference<T> {
            val windowSizeClass = calculateWindowSizeClass(LocalActivity.current!!)
            return when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> compact
                WindowWidthSizeClass.Medium -> medium
                WindowWidthSizeClass.Expanded -> expanded
                else -> compact
            }
        }
    }

    class ListViewOptionsPreference(
        val key: String,
        val defaultSorting: Int,
        val defaultSortingReversed: Boolean,
        val defaultListStyles: WindowSizeClassValueGroup<Int>,
        val defaultGridMaxLineSpans: WindowSizeClassValueGroup<Int>,
        private val intGetter: (key: String, defaultValue: Int) -> Int,
        private val intSetter: (key: String, newValue: Int) -> Unit,
        private val booleanGetter: (key: String, defaultValue: Boolean) -> Boolean,
        private val booleanSetter: (key: String, newValue: Boolean) -> Unit
    ) {
        private fun <PreferenceT> createSubPreference(
            suffix: String,
            defaultValue: PreferenceT,
            getter: (key: String, defaultValue: PreferenceT) -> PreferenceT,
            setter: (key: String, newValue: PreferenceT) -> Unit
        ): Preference<PreferenceT> = Preference(
            key = "$key.$suffix",
            defaultValue = defaultValue,
            getter, setter
        )

        private fun createSubIntPreference(suffix: String, defaultValue: Int): Preference<Int> = createSubPreference(
            suffix = suffix,
            defaultValue = defaultValue,
            intGetter, intSetter
        )

        private fun createSubBooleanPreference(suffix: String, defaultValue: Boolean): Preference<Boolean> = createSubPreference(
            suffix = suffix,
            defaultValue = defaultValue,
            booleanGetter, booleanSetter
        )

        private fun <SubT> createSubScreenSizeClassPreferenceGroup(
            suffix: String,
            defaultValues: WindowSizeClassValueGroup<SubT>,
            getter: (key: String, defaultValue: SubT) -> SubT,
            setter: (key: String, newValue: SubT) -> Unit
        ): WindowSizeClassPreferenceGroup<SubT> = WindowSizeClassPreferenceGroup(
            key = "$key.$suffix",
            defaultValues = defaultValues,
            getter, setter
        )

        val sorting = createSubIntPreference("sorting", defaultSorting)
        val sortingReversed = createSubBooleanPreference("sortingReversed", defaultSortingReversed)
        val styleGroup = createSubScreenSizeClassPreferenceGroup(
            suffix = "style",
            defaultValues = defaultListStyles,
            intGetter, intSetter
        )
        val gridMaxLineSpanGroup = createSubScreenSizeClassPreferenceGroup(
            suffix = "gridMaxLineSpan",
            defaultValues = defaultGridMaxLineSpans,
            intGetter, intSetter
        )
    }

    fun listViewOptionsPreference(
        key: String,
        defaultSorting: Int = ListSorting.ALPHABETICAL.ordinal,
        defaultSortingReversed: Boolean = false,
        defaultListStyles: WindowSizeClassValueGroup<Int> = WindowSizeClassValueGroup(
            compact = ListStyle.LIST.ordinal,
            medium = ListStyle.LIST.ordinal,
            expanded = ListStyle.LIST.ordinal
        ),
        defaultGridMaxLineSpans: WindowSizeClassValueGroup<Int> = WindowSizeClassValueGroup(
            compact = 3,
            medium = 4,
            expanded = 5
        )
    ): ListViewOptionsPreference = ListViewOptionsPreference(
        key = key,
        defaultSorting = defaultSorting,
        defaultSortingReversed = defaultSortingReversed,
        defaultListStyles = defaultListStyles,
        defaultGridMaxLineSpans = defaultGridMaxLineSpans,
        intGetter = ::getInt,
        intSetter = ::putInt,
        booleanGetter = ::getBoolean,
        booleanSetter = ::putBoolean
    )
}