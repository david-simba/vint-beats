package com.davidsimba.vintbeats.feature.library.data.playlist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.davidsimba.vintbeats.feature.library.data.track.SavedTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Transaction
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAll(): Flow<List<PlaylistWithTracksEntity>>

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlistId = :id")
    fun getById(id: Int): Flow<PlaylistWithTracksEntity?>

    @Query("""
        SELECT st.* FROM saved_tracks st
        INNER JOIN playlist_track_cross_ref ptcr ON st.id = ptcr.savedTrackId
        WHERE ptcr.playlistId = :playlistId
        ORDER BY ptcr.displayOrder ASC, ptcr.addedAt ASC
    """)
    fun getOrderedTracks(playlistId: Int): Flow<List<SavedTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE playlistId = :id")
    suspend fun deleteById(id: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrack(crossRef: PlaylistTrackCrossRef)

    @Delete
    suspend fun removeTrack(crossRef: PlaylistTrackCrossRef)

    @Query("SELECT * FROM playlists WHERE playlistId = :id LIMIT 1")
    suspend fun getPlaylistInfo(id: Int): PlaylistEntity?

    @Query("UPDATE playlists SET name = :name, coverImagePath = :coverImagePath WHERE playlistId = :playlistId")
    suspend fun updatePlaylist(playlistId: Int, name: String, coverImagePath: String?)

    @Query("SELECT COALESCE(MAX(displayOrder), -1) + 1 FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun nextDisplayOrder(playlistId: Int): Int

    @Query("UPDATE playlist_track_cross_ref SET displayOrder = :order WHERE playlistId = :playlistId AND savedTrackId = :savedTrackId")
    suspend fun updateTrackOrder(playlistId: Int, savedTrackId: Int, order: Int)
}
