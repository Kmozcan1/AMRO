package com.abn.amro.movies.ui.model

import com.abn.amro.core.ui.UiText

data class MovieDetailUiModel(
    val id: Long,
    val title: String,
    val tagline: String?,
    val backdropUrl: String?,
    val posterUrl: String?,
    val genres: String,
    val overview: String,
    val voteAverage: String,
    val voteCount: UiText,
    val budget: UiText,
    val revenue: UiText,
    val status: String,
    val runtime: UiText,
    val releaseDate: UiText,
    val imdbUrl: String?
)