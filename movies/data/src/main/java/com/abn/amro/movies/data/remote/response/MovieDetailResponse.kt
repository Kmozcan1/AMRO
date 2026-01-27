package com.abn.amro.movies.data.remote.response

import com.abn.amro.movies.data.remote.model.GenreDto
import com.google.gson.annotations.SerializedName

data class MovieDetailResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String?,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("genres") val genres: List<GenreDto>?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("budget") val budget: Long?,
    @SerializedName("revenue") val revenue: Long?,
    @SerializedName("status") val status: String?,
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("release_date") val releaseDate: String?
)