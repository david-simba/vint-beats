package com.davidsimba.vintbeats.core.database

import android.content.Context
import androidx.room.Room
import com.davidsimba.vintbeats.core.database.dao.PlaylistDao
import com.davidsimba.vintbeats.core.database.dao.RecentCollectionDao
import com.davidsimba.vintbeats.core.database.dao.SavedAlbumDao
import com.davidsimba.vintbeats.core.database.dao.SavedArtistDao
import com.davidsimba.vintbeats.core.database.dao.TrackDao
import com.davidsimba.vintbeats.core.database.dao.RecentlyPlayedAlbumDao
import com.davidsimba.vintbeats.core.database.dao.RecentlyPlayedDao
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
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTrackDao(db: VintBeatsDatabase): TrackDao = db.trackDao()

    @Provides
    fun providePlaylistDao(db: VintBeatsDatabase): PlaylistDao = db.playlistDao()

    @Provides
    fun provideSavedAlbumDao(db: VintBeatsDatabase): SavedAlbumDao = db.savedAlbumDao()

    @Provides
    fun provideSavedArtistDao(db: VintBeatsDatabase): SavedArtistDao = db.savedArtistDao()

    @Provides
    fun provideRecentlyPlayedDao(db: VintBeatsDatabase): RecentlyPlayedDao = db.recentlyPlayedDao()

    @Provides
    fun provideRecentlyPlayedAlbumDao(db: VintBeatsDatabase): RecentlyPlayedAlbumDao = db.recentlyPlayedAlbumDao()

    @Provides
    fun provideRecentCollectionDao(db: VintBeatsDatabase): RecentCollectionDao = db.recentCollectionDao()
}
