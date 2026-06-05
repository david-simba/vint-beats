package com.davidsimba.vintbeats.feature.home.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyPlayedAlbumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RecentlyPlayedAlbumEntity)

    @Query("SELECT * FROM recently_played_albums ORDER BY playedAt DESC LIMIT :limit")
    fun getRecent(limit: Int = 10): Flow<List<RecentlyPlayedAlbumEntity>>
}
