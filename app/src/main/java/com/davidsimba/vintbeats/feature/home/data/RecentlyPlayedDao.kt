package com.davidsimba.vintbeats.feature.home.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyPlayedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RecentlyPlayedEntity)

    @Query("SELECT * FROM recently_played ORDER BY playedAt DESC LIMIT :limit")
    fun getRecent(limit: Int = 10): Flow<List<RecentlyPlayedEntity>>

    @Query("UPDATE recently_played SET playedAt = :playedAt WHERE trackId = :trackId")
    suspend fun updateTimestamp(trackId: String, playedAt: Long = System.currentTimeMillis())
}
