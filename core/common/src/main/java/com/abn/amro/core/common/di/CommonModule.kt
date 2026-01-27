package com.abn.amro.core.common.di

import com.abn.amro.core.common.BuildConfig
import com.abn.amro.core.common.dispatcher.DefaultDispatcher
import com.abn.amro.core.common.dispatcher.IoDispatcher
import com.abn.amro.core.common.dispatcher.MainDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @TmdbImageBaseUrl
    fun provideTmdbImageBaseUrl(): String {
        return BuildConfig.TMDB_IMAGE_BASE_URL
    }
}
