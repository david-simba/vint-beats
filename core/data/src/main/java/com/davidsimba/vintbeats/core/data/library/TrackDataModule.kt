package com.davidsimba.vintbeats.core.data.library

import com.davidsimba.vintbeats.core.data.library.SavedAlbumRepositoryImpl
import com.davidsimba.vintbeats.core.data.library.SavedArtistRepositoryImpl
import com.davidsimba.vintbeats.core.data.library.PlaylistRepositoryImpl
import com.davidsimba.vintbeats.core.data.library.TrackRepositoryImpl
import com.davidsimba.vintbeats.core.data.library.SavedAlbumRepository
import com.davidsimba.vintbeats.core.data.library.SavedArtistRepository
import com.davidsimba.vintbeats.core.data.library.PlaylistRepository
import com.davidsimba.vintbeats.core.data.library.TrackRepository
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
