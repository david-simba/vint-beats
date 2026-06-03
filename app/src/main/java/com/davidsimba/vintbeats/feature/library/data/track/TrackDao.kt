package com.davidsimba.vintbeats.feature.library.data.track

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM saved_tracks ORDER BY savedAt DESC")
    fun getAllTracks(): Flow<List<SavedTrackEntity>>

    @Query("SELECT * FROM saved_tracks WHERE audioFilePath IS NOT NULL ORDER BY savedAt DESC")
    fun getDownloadedTracks(): Flow<List<SavedTrackEntity>>

    @Query("SELECT * FROM saved_tracks WHERE isFavorite = 1 ORDER BY savedAt DESC")
    fun getFavoriteTracks(): Flow<List<SavedTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: SavedTrackEntity)

    @Query("SELECT * FROM saved_tracks WHERE id = :id")
    suspend fun getById(id: Int): SavedTrackEntity?

    @Query("SELECT * FROM saved_tracks WHERE trackId = :trackId LIMIT 1")
    suspend fun getByTrackId(trackId: String): SavedTrackEntity?

    @Query("UPDATE saved_tracks SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Int, isFavorite: Boolean)

    @Query("DELETE FROM saved_tracks WHERE id = :id")
    suspend fun deleteById(id: Int)
}
