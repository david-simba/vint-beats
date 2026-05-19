package com.davidsimba.vintbeats.feature.search.data

import com.davidsimba.vintbeats.feature.search.domain.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchModule {

    @Binds
    @Singleton
    abstract fun searchRepository(impl: SearchRepositoryImpl): SearchRepository
}
