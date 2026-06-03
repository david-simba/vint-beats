package com.davidsimba.vintbeats.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.davidsimba.vintbeats.feature.library.data.playlist.PlaylistDao
import com.davidsimba.vintbeats.feature.library.data.playlist.PlaylistEntity
import com.davidsimba.vintbeats.feature.library.data.playlist.PlaylistTrackCrossRef
import com.davidsimba.vintbeats.feature.library.data.album.SavedAlbumDao
import com.davidsimba.vintbeats.feature.library.data.album.SavedAlbumEntity
import com.davidsimba.vintbeats.feature.library.data.artist.SavedArtistDao
import com.davidsimba.vintbeats.feature.library.data.artist.SavedArtistEntity
import com.davidsimba.vintbeats.feature.library.data.track.SavedTrackEntity
import com.davidsimba.vintbeats.feature.library.data.track.TrackDao

@Database(
    entities = [
        SavedTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        SavedAlbumEntity::class,
        SavedArtistEntity::class,
    ],
    version = 8,
    exportSchema = false
)
abstract class VintBeatsDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun savedAlbumDao(): SavedAlbumDao
    abstract fun savedArtistDao(): SavedArtistDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE saved_cassettes ADD COLUMN audioFilePath TEXT")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS saved_tracks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        trackId TEXT NOT NULL,
                        trackTitle TEXT NOT NULL,
                        trackArtist TEXT NOT NULL,
                        trackThumbnailUrl TEXT,
                        trackDurationText TEXT NOT NULL,
                        savedAt INTEGER NOT NULL,
                        audioFilePath TEXT
                    )
                    """.trimIndent()
                )
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE saved_tracks ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
            }
        }
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS playlists (
                        playlistId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS playlist_track_cross_ref (
                        playlistId INTEGER NOT NULL,
                        savedTrackId INTEGER NOT NULL,
                        addedAt INTEGER NOT NULL,
                        PRIMARY KEY(playlistId, savedTrackId)
                    )
                    """.trimIndent()
                )
            }
        }
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE playlists ADD COLUMN coverImagePath TEXT")
            }
        }
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE playlist_track_cross_ref ADD COLUMN displayOrder INTEGER NOT NULL DEFAULT 0")
            }
        }
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS saved_albums (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        albumId TEXT NOT NULL,
                        title TEXT NOT NULL,
                        artist TEXT NOT NULL,
                        thumbnailUrl TEXT,
                        savedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS saved_artists (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        artistId TEXT NOT NULL,
                        name TEXT NOT NULL,
                        thumbnailUrl TEXT,
                        savedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
