package com.davidsimba.vintbeats.feature.library.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM saved_tracks ORDER BY savedAt DESC")
    fun getAllTracks(): Flow<List<SavedTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: SavedTrackEntity)

    @Query("SELECT * FROM saved_tracks WHERE id = :id")
    suspend fun getById(id: Int): SavedTrackEntity?

    @Query("DELETE FROM saved_tracks WHERE id = :id")
    suspend fun deleteById(id: Int)
}
