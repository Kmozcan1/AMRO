package com.abn.amro.movies.domain.model

data class MovieDetail(
    val id: Long,
    val title: String,
    val tagline: String?,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val genres: List<Genre>,
    val voteAverage: Double,
    val voteCount: Int,
    val budget: Long,
    val revenue: Long,
    val status: String,
    val imdbId: String?,
    val runtime: Int,
    val releaseDate: String?
)

val MovieDetail.imdbUrl: String?
    get() = imdbId?.let { "$IMDB_BASE_URL$it" }

private const val IMDB_BASE_URL = "https://www.imdb.com/title/"