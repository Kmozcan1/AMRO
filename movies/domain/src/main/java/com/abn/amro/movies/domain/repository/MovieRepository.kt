package com.abn.amro.movies.domain.repository

import com.abn.amro.core.common.result.AmroResult
import com.abn.amro.movies.domain.model.Genre
import com.abn.amro.movies.domain.model.Movie
import com.abn.amro.movies.domain.model.MovieDetail
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getTop100Movies(): Flow<AmroResult<List<Movie>>>

    fun getMovieDetails(movieId: Long): Flow<AmroResult<MovieDetail>>

    fun getMovieGenres(): Flow<AmroResult<List<Genre>>>
}