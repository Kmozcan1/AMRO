package com.abn.amro.movies.ui.mapper

import com.abn.amro.movies.domain.model.Movie
import com.abn.amro.movies.ui.formatRating
import com.abn.amro.movies.ui.model.MovieUiModel

fun Movie.toUiModel(imageBaseUrl: String): MovieUiModel {
    return MovieUiModel(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterPath?.let { "$imageBaseUrl$it" },
        releaseDate = releaseDate,
        voteAverage = voteAverage.formatRating(),
        genreIds = genreIds
    )
}