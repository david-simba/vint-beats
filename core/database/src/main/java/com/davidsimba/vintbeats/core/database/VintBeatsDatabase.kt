package com.davidsimba.vintbeats.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.davidsimba.vintbeats.core.database.dao.RecentCollectionDao
import com.davidsimba.vintbeats.core.database.entity.RecentCollectionEntity
import com.davidsimba.vintbeats.core.database.dao.RecentlyPlayedAlbumDao
import com.davidsimba.vintbeats.core.database.entity.RecentlyPlayedAlbumEntity
import com.davidsimba.vintbeats.core.database.dao.RecentlyPlayedDao
import com.davidsimba.vintbeats.core.database.entity.RecentlyPlayedEntity
import com.davidsimba.vintbeats.core.database.dao.SavedAlbumDao
import com.davidsimba.vintbeats.core.database.entity.SavedAlbumEntity
import com.davidsimba.vintbeats.core.database.dao.SavedArtistDao
import com.davidsimba.vintbeats.core.database.entity.SavedArtistEntity
import com.davidsimba.vintbeats.core.database.dao.PlaylistDao
import com.davidsimba.vintbeats.core.database.entity.PlaylistEntity
import com.davidsimba.vintbeats.core.database.entity.PlaylistTrackCrossRef
import com.davidsimba.vintbeats.core.database.entity.SavedTrackEntity
import com.davidsimba.vintbeats.core.database.dao.TrackDao

@Database(
    entities = [
        SavedTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        SavedAlbumEntity::class,
        SavedArtistEntity::class,
        RecentlyPlayedEntity::class,
        RecentlyPlayedAlbumEntity::class,
        RecentCollectionEntity::class,
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
    abstract fun recentlyPlayedAlbumDao(): RecentlyPlayedAlbumDao
    abstract fun recentCollectionDao(): RecentCollectionDao
}
