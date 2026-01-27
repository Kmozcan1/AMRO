package com.abn.amro.movies.data.repository

import com.abn.amro.core.common.dispatcher.IoDispatcher
import com.abn.amro.core.common.result.AmroResult
import com.abn.amro.core.common.result.asResult
import com.abn.amro.movies.data.mapper.toDomain
import com.abn.amro.movies.data.remote.TmdbApiService
import com.abn.amro.movies.domain.model.Genre
import com.abn.amro.movies.domain.model.Movie
import com.abn.amro.movies.domain.model.MovieDetail
import com.abn.amro.movies.domain.repository.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApiService,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MovieRepository {

    override fun getTop100Movies(): Flow<AmroResult<List<Movie>>> = flow {
        val movies = coroutineScope {
            val top100Movies = (1..5).map { page ->
                async {
                    try {
                        api.getTrendingMovies(page = page).results
                    } catch (e: Exception) {
                        throw e
                    }
                }
            }

            top100Movies.awaitAll().flatten()
        }

        emit(movies.map { it.toDomain() }.distinctBy { it.id })
    }
        .asResult()
        .flowOn(ioDispatcher)

    override fun getMovieDetails(movieId: Long): Flow<AmroResult<MovieDetail>> = flow {
        val dto = api.getMovieDetails(movieId = movieId)
        emit(dto.toDomain())
    }
        .asResult()
        .flowOn(ioDispatcher)

    override fun getMovieGenres(): Flow<AmroResult<List<Genre>>> = flow {
        val response = api.getMovieGenres()
        emit(response.genres.map { it.toDomain() }.distinctBy { it.id })
    }
        .asResult()
        .flowOn(ioDispatcher)
}