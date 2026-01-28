package com.abn.amro.movies.ui.model

import com.abn.amro.core.ui.model.UiText

data class MovieUiModel(
    val id: Long,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val releaseDate: UiText?,
    val voteAverage: UiText,
    val voteCount: UiText,
    val genres: List<String>
)