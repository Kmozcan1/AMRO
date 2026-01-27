package com.abn.amro.movies.data.remote.response

import com.abn.amro.movies.data.remote.model.MovieDto
import com.google.gson.annotations.SerializedName

data class TrendingMoviesResult(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<MovieDto>,
    @SerializedName("total_pages") val totalPages: Int
)