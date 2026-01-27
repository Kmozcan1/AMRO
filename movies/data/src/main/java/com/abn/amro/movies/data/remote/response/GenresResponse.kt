package com.abn.amro.movies.data.remote.response

import com.abn.amro.movies.data.remote.model.GenreDto
import com.google.gson.annotations.SerializedName

data class GenresResponse(
    @SerializedName("genres") val genres: List<GenreDto>
)