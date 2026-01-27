package com.abn.amro.movies.ui.mapper

import com.abn.amro.movies.domain.model.Movie
import com.abn.amro.movies.ui.model.MovieUiModel
import java.util.Locale

fun Movie.toUiModel(imageBaseUrl: String): MovieUiModel {
    return MovieUiModel(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterPath?.let { "$imageBaseUrl$it" },
        releaseDate = releaseDate ?: "Unknown Date",
        voteAverage = String.format(Locale.US, "%.1f", voteAverage),
        genreIds = genreIds
    )
}