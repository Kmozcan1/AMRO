package com.abn.amro.movies.data.remote

import com.abn.amro.movies.data.remote.response.GenresResponse
import com.abn.amro.movies.data.remote.response.MovieDetailResponse
import com.abn.amro.movies.data.remote.response.Top100MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("page") page: Int = 0,
    ): Top100MoviesResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Long
    ): MovieDetailResponse

    @GET("genre/movie/list")
    suspend fun getMovieGenres(): GenresResponse
}