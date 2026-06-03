package com.davidsimba.vintbeats.core.database

import android.content.Context
import androidx.room.Room
import com.davidsimba.vintbeats.feature.library.data.playlist.PlaylistDao
import com.davidsimba.vintbeats.feature.library.data.album.SavedAlbumDao
import com.davidsimba.vintbeats.feature.library.data.artist.SavedArtistDao
import com.davidsimba.vintbeats.feature.library.data.track.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VintBeatsDatabase =
        Room.databaseBuilder(context, VintBeatsDatabase::class.java, "vintbeats.db")
//            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTrackDao(db: VintBeatsDatabase): TrackDao = db.trackDao()

    @Provides
    fun providePlaylistDao(db: VintBeatsDatabase): PlaylistDao = db.playlistDao()

    @Provides
    fun provideSavedAlbumDao(db: VintBeatsDatabase): SavedAlbumDao = db.savedAlbumDao()

    @Provides
    fun provideSavedArtistDao(db: VintBeatsDatabase): SavedArtistDao = db.savedArtistDao()
}
