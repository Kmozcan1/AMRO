package com.abn.amro.movies.data.mapper

import com.abn.amro.movies.data.remote.model.MovieDto
import com.abn.amro.movies.data.remote.response.TrendingMoviesResponse
import com.abn.amro.movies.domain.model.Movie

fun TrendingMoviesResponse.toDomain(): List<Movie> {
    return results.map { it.toDomain() }
}

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        popularity = popularity ?: 0.0,
        voteAverage = voteAverage ?: 0.0,
        genreIds = genreIds ?: emptyList()
    )
}