package com.davidsimba.vintbeats.core.data.library

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackDataModule {

    @Binds
    @Singleton
    abstract fun bindTrackRepository(impl: TrackRepositoryImpl): TrackRepository

    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(impl: PlaylistRepositoryImpl): PlaylistRepository

    @Binds
    @Singleton
    abstract fun bindSavedAlbumRepository(impl: SavedAlbumRepositoryImpl): SavedAlbumRepository

    @Binds
    @Singleton
    abstract fun bindSavedArtistRepository(impl: SavedArtistRepositoryImpl): SavedArtistRepository
}
