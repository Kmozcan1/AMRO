package com.abn.amro.movies.data.mapper

import com.abn.amro.movies.data.remote.model.GenreDto
import com.abn.amro.movies.data.remote.response.MovieDetailResponse
import com.abn.amro.movies.domain.model.Genre
import com.abn.amro.movies.domain.model.MovieDetail

fun MovieDetailResponse.toDomain(): MovieDetail {
    return MovieDetail(
        id = id,
        title = title.orEmpty(),
        tagline = tagline,
        overview = overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath,
        genres = genres?.map { it.toDomain() } ?: emptyList(),
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        budget = budget ?: 0L,
        revenue = revenue ?: 0L,
        status = status.orEmpty(),
        imdbId = imdbId,
        runtime = runtime ?: 0,
        releaseDate = releaseDate
    )
}

fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name.orEmpty()
    )
}
