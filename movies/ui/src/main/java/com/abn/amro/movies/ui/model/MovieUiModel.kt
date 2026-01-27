package com.abn.amro.movies.ui.model

data class MovieUiModel(
    val id: Long,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val releaseDate: String?,
    val voteAverage: String,
    val genreIds: List<Int>
)