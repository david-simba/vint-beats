package com.davidsimba.vintbeats.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.davidsimba.vintbeats.feature.home.data.RecentlyPlayedDao
import com.davidsimba.vintbeats.feature.home.data.RecentlyPlayedEntity
import com.davidsimba.vintbeats.feature.library.data.album.SavedAlbumDao
import com.davidsimba.vintbeats.feature.library.data.album.SavedAlbumEntity
import com.davidsimba.vintbeats.feature.library.data.artist.SavedArtistDao
import com.davidsimba.vintbeats.feature.library.data.artist.SavedArtistEntity
import com.davidsimba.vintbeats.feature.library.data.playlist.PlaylistDao
import com.davidsimba.vintbeats.feature.library.data.playlist.PlaylistEntity
import com.davidsimba.vintbeats.feature.library.data.playlist.PlaylistTrackCrossRef
import com.davidsimba.vintbeats.feature.library.data.track.SavedTrackEntity
import com.davidsimba.vintbeats.feature.library.data.track.TrackDao

@Database(
    entities = [
        SavedTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        SavedAlbumEntity::class,
        SavedArtistEntity::class,
        RecentlyPlayedEntity::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class VintBeatsDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun savedAlbumDao(): SavedAlbumDao
    abstract fun savedArtistDao(): SavedArtistDao
    abstract fun recentlyPlayedDao(): RecentlyPlayedDao
}
