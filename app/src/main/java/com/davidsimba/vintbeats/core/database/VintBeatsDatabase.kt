package com.davidsimba.vintbeats.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.davidsimba.vintbeats.feature.cassette.data.CassetteDao
import com.davidsimba.vintbeats.feature.cassette.data.SavedCassetteEntity

@Database(entities = [SavedCassetteEntity::class], version = 2, exportSchema = false)
abstract class VintBeatsDatabase : RoomDatabase() {
    abstract fun cassetteDao(): CassetteDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE saved_cassettes ADD COLUMN audioFilePath TEXT")
            }
        }
    }
}
