package com.davidsimba.vintbeats.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.davidsimba.vintbeats.core.database.entity.SavedArtistEntity

@Dao
interface SavedArtistDao {
    @Query("SELECT * FROM saved_artists ORDER BY savedAt DESC")
    fun getAll(): Flow<List<SavedArtistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artist: SavedArtistEntity)

    @Query("DELETE FROM saved_artists WHERE artistId = :artistId")
    suspend fun deleteByArtistId(artistId: String)

    @Query("SELECT COUNT(*) FROM saved_artists WHERE artistId = :artistId")
    suspend fun countByArtistId(artistId: String): Int
}
