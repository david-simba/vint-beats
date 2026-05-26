package com.davidsimba.vintbeats.feature.album.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlbumModule {
    @Binds
    @Singleton
    abstract fun albumRepository(impl: AlbumRepositoryImpl): AlbumRepository
}
