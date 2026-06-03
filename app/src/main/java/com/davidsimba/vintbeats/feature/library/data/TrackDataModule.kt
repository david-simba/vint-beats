package com.davidsimba.vintbeats.feature.library.data

import com.davidsimba.vintbeats.feature.library.data.album.SavedAlbumRepositoryImpl
import com.davidsimba.vintbeats.feature.library.data.artist.SavedArtistRepositoryImpl
import com.davidsimba.vintbeats.feature.library.data.playlist.PlaylistRepositoryImpl
import com.davidsimba.vintbeats.feature.library.data.track.TrackRepositoryImpl
import com.davidsimba.vintbeats.feature.library.domain.album.SavedAlbumRepository
import com.davidsimba.vintbeats.feature.library.domain.artist.SavedArtistRepository
import com.davidsimba.vintbeats.feature.library.domain.playlist.PlaylistRepository
import com.davidsimba.vintbeats.feature.library.domain.track.TrackRepository
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
