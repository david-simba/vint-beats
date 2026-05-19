package com.davidsimba.vintbeats.feature.search.data

import com.davidsimba.vintbeats.feature.search.domain.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchModule {

    @Binds
    @Singleton
    abstract fun searchRepository(
        impl: SearchRepositoryImpl
    ): SearchRepository

    companion object {
        @Provides
        @Singleton
        fun provideSpotifySearchApi(retrofit: Retrofit) : SpotifySearchApi =
            retrofit.create(SpotifySearchApi::class.java)
    }
}