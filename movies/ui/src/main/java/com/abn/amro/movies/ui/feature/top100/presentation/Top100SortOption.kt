package com.abn.amro.movies.ui.feature.top100.presentation

import androidx.annotation.StringRes
import com.abn.amro.movies.ui.R

enum class SortType(@param:StringRes val labelRes: Int) {
    POPULARITY(R.string.sort_popularity),
    TITLE(R.string.sort_title),
    RELEASE_DATE(R.string.sort_release_date)
}

enum class SortOrder() {
    Ascending,
    Descending
}

data class Top100SortConfig(
    val type: SortType = SortType.POPULARITY,
    val order: SortOrder = SortOrder.Descending
)
