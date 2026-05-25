package com.davidsimba.vintbeats.feature.artist.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ArtistModule {
    @Binds
    @Singleton
    abstract fun artistRepository(impl: ArtistRepositoryImpl): ArtistRepository
}
