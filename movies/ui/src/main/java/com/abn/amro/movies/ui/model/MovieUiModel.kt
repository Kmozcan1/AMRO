package com.abn.amro.movies.ui.model

import com.abn.amro.core.ui.UiText

data class MovieUiModel(
    val id: Long,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val releaseDate: UiText?,
    val voteAverage: UiText,
    val genreIds: List<Int>
)