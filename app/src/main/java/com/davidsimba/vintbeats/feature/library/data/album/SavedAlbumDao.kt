package com.davidsimba.vintbeats.feature.library.data.album

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedAlbumDao {
    @Query("SELECT * FROM saved_albums ORDER BY savedAt DESC")
    fun getAll(): Flow<List<SavedAlbumEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(album: SavedAlbumEntity)

    @Query("DELETE FROM saved_albums WHERE albumId = :albumId")
    suspend fun deleteByAlbumId(albumId: String)

    @Query("SELECT COUNT(*) FROM saved_albums WHERE albumId = :albumId")
    suspend fun countByAlbumId(albumId: String): Int
}
