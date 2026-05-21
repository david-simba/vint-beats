package com.davidsimba.vintbeats.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.davidsimba.vintbeats.feature.cassette.data.CassetteDao
import com.davidsimba.vintbeats.feature.cassette.data.SavedCassetteEntity

@Database(entities = [SavedCassetteEntity::class], version = 1, exportSchema = false)
abstract class VintBeatsDatabase : RoomDatabase() {
    abstract fun cassetteDao(): CassetteDao
}
