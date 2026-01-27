package com.abn.amro.movies.data.mapper

import com.abn.amro.core.network.BuildConfig
import com.abn.amro.movies.data.remote.model.MovieDto
import com.abn.amro.movies.data.remote.response.Top100MoviesResponse
import com.abn.amro.movies.domain.model.Movie

fun Top100MoviesResponse.toDomain(): List<Movie> {
    return results.map { it.toDomain() }
}

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = posterPath?.let { "${BuildConfig.TMDB_IMAGE_BASE_URL}$it" },
        backdropPath = backdropPath?.let { "${BuildConfig.TMDB_IMAGE_BASE_URL}$it" },
        releaseDate = releaseDate,
        popularity = popularity ?: 0.0,
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        genreIds = genreIds ?: emptyList()
    )
}
