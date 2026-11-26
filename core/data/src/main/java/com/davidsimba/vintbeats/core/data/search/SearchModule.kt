package com.davidsimba.vintbeats.core.data.search

import com.davidsimba.vintbeats.core.data.search.SearchRepository
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
