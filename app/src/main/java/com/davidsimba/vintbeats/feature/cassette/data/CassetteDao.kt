package com.davidsimba.vintbeats.feature.cassette.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CassetteDao {
    @Query("SELECT * FROM saved_cassettes ORDER BY savedAt DESC")
    fun getAllCassettes(): Flow<List<SavedCassetteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cassette: SavedCassetteEntity)

    @Query("DELETE FROM saved_cassettes WHERE id = :id")
    suspend fun deleteById(id: Int)
}
