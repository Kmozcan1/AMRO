package com.abn.amro.movies.data.di

import com.abn.amro.movies.data.remote.TmdbApiService
import com.abn.amro.movies.data.repository.MovieRepositoryImpl
import com.abn.amro.movies.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository

    companion object {
        @Provides
        @Singleton
        fun provideTmdbApiService(retrofit: Retrofit): TmdbApiService {
            return retrofit.create(TmdbApiService::class.java)
        }
    }
}
