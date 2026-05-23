package com.davidsimba.vintbeats.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.davidsimba.vintbeats.feature.library.data.SavedTrackEntity
import com.davidsimba.vintbeats.feature.library.data.TrackDao

@Database(
    entities = [SavedTrackEntity::class],
    version = 3,
    exportSchema = false
)
abstract class VintBeatsDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao

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
    }
}
